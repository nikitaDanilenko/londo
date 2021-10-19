package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.Transactionally
import db.generated.daos._
import db.keys.{ RegistrationTokenId, SessionKeyId }
import db.models.{ RegistrationToken, SessionKey }
import doobie.ConnectionIO
import errors.{ ErrorContext, ServerError }
import security.Hash
import security.jwt.{ JwtConfiguration, JwtExpiration }
import services.email.{ EmailParameters, EmailService }
import spire.math.Natural
import utils.jwt.JwtUtil
import utils.random.RandomGenerator
import utils.time.TimeUtil

import javax.inject.Inject

class UserService @Inject() (
    userDAO: UserDAO,
    userSettingsDAO: UserSettingsDAO,
    userDetailsDAO: UserDetailsDAO,
    sessionKeyDAO: SessionKeyDAO,
    registrationTokenDAO: RegistrationTokenDAO,
    emailService: EmailService,
    jwtConfiguration: JwtConfiguration,
    frontendConfiguration: FrontendConfiguration,
    transactionally: Transactionally
) {

  def login[F[_]: Async: ContextShift](
      nickname: String,
      password: String,
      isValidityUnrestricted: Boolean
  ): F[ServerError.Or[String]] = {
    EitherT(
      userDAO
        .findByNickname(nickname)
        .map(
          _.headOption
            .toRight(ErrorContext.Login.Failure.asServerError)
        )
    ).flatMapF { user =>
      val isValid = Hash.verify(
        password = password,
        passwordParameters = PasswordParameters(
          hash = user.passwordHash,
          salt = user.passwordSalt,
          iterations = Natural(user.iterations)
        )
      )
      if (isValid) {
        for {
          sessionKey <- Async[F].liftIO(RandomGenerator.randomUUID)
          _ <- sessionKeyDAO.replace(SessionKey(user.id, sessionKey))
          r <-
            Async[F]
              .liftIO(
                generateUserAuthentication(
                  userId = UserId(user.id),
                  sessionId = SessionId(sessionKey),
                  isValidityUnrestricted = isValidityUnrestricted
                ).map(
                  _.asRight[ServerError]
                )
              )
        } yield r
      } else ErrorContext.Login.Failure.asServerError.asLeft[String].pure
    }.value
  }

  def generateUserAuthentication(userId: UserId, sessionId: SessionId, isValidityUnrestricted: Boolean): IO[String] = {
    TimeUtil.nowSeconds
      .map { now =>
        val jwtExpiration =
          if (isValidityUnrestricted) JwtExpiration.Never
          else
            JwtExpiration.Expiring(
              start = now,
              duration = jwtConfiguration.restrictedDurationInSeconds
            )
        JwtUtil.createJwt(
          userId = userId,
          sessionId = sessionId,
          privateKey = jwtConfiguration.signaturePrivateKey,
          expiration = jwtExpiration
        )
      }
  }

  def fetch[F[_]: Async: ContextShift](userId: UserId): F[ServerError.Or[User]] = {
    val dbUserId = UserId.toDb(userId)
    val transformer = for {
      userRow <- EitherT.fromOptionF(userDAO.find(dbUserId), ErrorContext.User.NotFound.asServerError)
      userSettings <-
        EitherT.fromOptionF(userSettingsDAO.find(dbUserId), ErrorContext.User.Settings.NotFound.asServerError)
      userDetails <-
        EitherT.fromOptionF(userDetailsDAO.find(dbUserId), ErrorContext.User.Details.NotFound.asServerError)
    } yield User.fromRow(
      userRow = userRow,
      settings = UserSettings.fromRow(userSettings),
      details = UserDetails.fromRow(userDetails)
    )
    transformer.value
  }

  def logout[F[_]: Async: ContextShift](userId: UserId, sessionId: SessionId): F[Unit] =
    sessionKeyDAO
      .delete(SessionKeyId(UserId.toDb(userId), SessionId.toDb(sessionId)))
      .void

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[ServerError.Or[User]] = {
    val registrationTokenId = RegistrationTokenId(userCreation.email)

    val transformer = for {
      localToken <- EitherT.fromOptionF[ConnectionIO, ServerError, RegistrationToken](
        registrationTokenDAO.findC(registrationTokenId),
        ErrorContext.Registration.NoRegistrationTokenForEmail.asServerError
      )
      _ <- EitherT.cond[ConnectionIO](
        localToken.token == userCreation.token,
        (),
        ErrorContext.Authentication.Token.Registration.asServerError
      )
      createdUser <- ServerError.liftC(Async[ConnectionIO].liftIO(UserCreation.create(userCreation)))
      userRow <- EitherT(userDAO.insertC(User.toRow(createdUser.user, createdUser.passwordParameters)))
        .leftMap(_ => ErrorContext.User.Create.asServerError)
      settingsRow <- EitherT(
        userSettingsDAO.insertC(UserSettings.toRow(createdUser.user.id, createdUser.user.settings))
      ).leftMap(_ => ErrorContext.User.Settings.Create.asServerError)
      detailsRow <-
        EitherT(userDetailsDAO.insertC(UserDetails.toRow(createdUser.user.id, createdUser.user.details))).leftMap(_ =>
          ErrorContext.User.Details.Create.asServerError
        )
      _ <- EitherT(registrationTokenDAO.deleteC(registrationTokenId)).leftMap(_ =>
        ErrorContext.Registration.Delete.asServerError
      )
    } yield User.fromRow(userRow, UserSettings.fromRow(settingsRow), UserDetails.fromRow(detailsRow))

    transactionally(transformer.value)
  }

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    userDAO
      .delete(UserId.toDb(userId))
      .void

  def requestCreate[F[_]: Async: ContextShift](email: String): F[ServerError.Or[Unit]] =
    for {
      users <- userDAO.findByEmail(email)
      // TODO: Improve structure, the current one seems a little awkward.
      response <- {
        if (users.nonEmpty)
          EitherT
            .leftT[F, Unit](ErrorContext.Registration.EmailAlreadyRegistered.asServerError)
            .value
        else {
          val transformer = for {
            token <- EitherT.liftF[F, ServerError, String](
              Async[F].liftIO(RandomGenerator.randomAlphaNumericString(UserService.registrationTokenLength))
            )
            registrationToken <- EitherT(registrationTokenDAO.replace(RegistrationToken(email, token)))
              .leftMap(_ => ErrorContext.Registration.Replace.asServerError)
            response <- EitherT.liftF[F, ServerError, Unit](
              Async[F].liftIO(
                emailService.sendEmail(
                  EmailParameters(
                    from = UserService.londoSenderAddress,
                    to = registrationToken.email,
                    // TODO: Add more explanation text to email
                    content = RegistrationToken.createRegistrationLink(
                      frontendConfiguration = frontendConfiguration,
                      registrationToken = registrationToken
                    )
                  )
                )
              )
            )
          } yield response
          transformer.value
        }
      }
    } yield response

  // TODO: Add update function

}

object UserService {
  val registrationTokenLength: Natural = Natural(64)
  // TODO: Add proper sending address (via application conf)
  val londoSenderAddress: String = "noreply@londo.io"
}
