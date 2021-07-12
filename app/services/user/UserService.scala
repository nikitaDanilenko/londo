package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.Transactionally
import db.generated.daos._
import db.keys.RegistrationTokenId
import db.models.{ RegistrationToken, SessionKey }
import doobie.ConnectionIO
import errors.ServerError
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
    transactionally: Transactionally
) {

  def login[F[_]: Async: ContextShift](
      nickname: String,
      password: String,
      publicSignatureKey: String,
      isValidityUnrestricted: Boolean
  ): F[ServerError.Or[String]] = {
    EitherT(
      userDAO
        .findByNickname(nickname)
        .map(
          _.headOption
            .toRight(ServerError.Login.Failure)
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
      if (isValid)
        sessionKeyDAO
          .replace(SessionKey(user.id, publicSignatureKey))
          .flatMap(_ =>
            Async[F]
              .liftIO(generateUserAuthentication(UserId(user.id), isValidityUnrestricted).map(_.asRight[ServerError]))
          )
      else (ServerError.Login.Failure: ServerError).asLeft[String].pure
    }.value
  }

  def generateUserAuthentication(userId: UserId, isValidityUnrestricted: Boolean): IO[String] = {
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
          privateKey = jwtConfiguration.signaturePrivateKey,
          expiration = jwtExpiration
        )
      }
  }

  def fetch[F[_]: Async: ContextShift](userId: UserId): F[ServerError.Or[User]] = {
    val dbUserId = UserId.toDb(userId)
    val transformer = for {
      userRow <- EitherT.fromOptionF(userDAO.find(dbUserId), ServerError.User.NotFound)
      userSettings <- EitherT.fromOptionF(userSettingsDAO.find(dbUserId), ServerError.User.SettingsNotFound)
      userDetails <- EitherT.fromOptionF(userDetailsDAO.find(dbUserId), ServerError.User.DetailsNotFound: ServerError)
    } yield User.fromRow(
      userRow = userRow,
      settings = UserSettings.fromRow(userSettings),
      details = UserDetails.fromRow(userDetails)
    )
    transformer.value
  }

  def logout[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    sessionKeyDAO
      .delete(UserId.toDb(userId))
      .void

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[ServerError.Or[User]] = {
    val registrationTokenId = RegistrationTokenId(userCreation.email)

    def lift[A](c: ConnectionIO[A]): EitherT[ConnectionIO, ServerError, A] =
      EitherT.liftF[ConnectionIO, ServerError, A](c)

    val transformer = for {
      localToken <- EitherT.fromOptionF[ConnectionIO, ServerError, RegistrationToken](
        registrationTokenDAO.findC(registrationTokenId),
        ServerError.Registration.NoRegistrationTokenForEmail: ServerError
      )
      _ <- EitherT.cond[ConnectionIO](
        localToken.token == userCreation.token,
        (),
        ServerError.Authentication.Token.Registration: ServerError
      )
      createdUser <- EitherT.liftF(Async[ConnectionIO].liftIO(UserCreation.create(userCreation)))
      userRow <- lift(userDAO.insertC(User.toRow(createdUser.user, createdUser.passwordParameters)))
      settingsRow <- lift(userSettingsDAO.insertC(UserSettings.toRow(createdUser.user.id, createdUser.user.settings)))
      detailsRow <- lift(userDetailsDAO.insertC(UserDetails.toRow(createdUser.user.id, createdUser.user.details)))
      _ <- lift(registrationTokenDAO.deleteC(registrationTokenId))
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
          Async[F].pure(Left(ServerError.Registration.EmailAlreadyRegistered: ServerError))
        else
          for {
            token <- Async[F].liftIO(RandomGenerator.randomAlphaNumericString(UserService.registrationTokenLength))
            registrationToken <- registrationTokenDAO.replace(RegistrationToken(email, token))
            response <- Async[F].liftIO(
              emailService.sendEmail(
                EmailParameters(
                  from = UserService.londoSenderAddress,
                  to = registrationToken.email,
                  // TODO: Add more explanation text to email
                  content = registrationToken.token
                )
              )
            )
          } yield Right(response)
      }
    } yield response

  // TODO: Add update function

}

object UserService {
  val registrationTokenLength: Natural = Natural(64)
  // TODO: Add proper sending address (via application conf)
  val londoSenderAddress: String = "noreply@londo.io"
}
