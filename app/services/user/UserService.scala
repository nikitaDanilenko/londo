package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.DbTransactorProvider
import db.generated.daos._
import db.keys.RegistrationTokenId
import db.models.{ RegistrationToken, SessionKey }
import doobie.syntax.connectionio._
import errors.{ ServerError, ServerException }
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
    dbTransactorProvider: DbTransactorProvider
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

  def fetch[F[_]: Async: ContextShift](userId: UserId): F[User] = {
    val dbUserId = UserId.toDb(userId)
    for {
      userRowCandidate <- userDAO.find(dbUserId)
      userRow <- Async[F].fromOption(userRowCandidate, ServerException(ServerError.User.NotFound))
      userSettings <- userSettingsDAO.find(dbUserId)
      userDetails <- userDetailsDAO.find(dbUserId)
    } yield User.fromRow(
      userRow = userRow,
      settings = userSettings.fold(UserSettings.default)(UserSettings.fromRow),
      details = userDetails.fold(UserDetails.default)(UserDetails.fromRow)
    )
  }

  def logout[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    sessionKeyDAO
      .delete(UserId.toDb(userId))
      .void

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[User] = {
    val registrationTokenId = RegistrationTokenId(userCreation.email)
    for {
      localToken <- registrationTokenDAO.find(registrationTokenId)
      _ <- Async[F].fromOption(
        localToken.filter(_.token == userCreation.token),
        ServerException(ServerError.Authentication.Token.Registration)
      )
      createdUser <- Async[F].liftIO(UserCreation.create(userCreation))
      userCreationAction = for {
        u <- userDAO.insertC(User.toRow(createdUser.user, createdUser.passwordParameters))
        s <- userSettingsDAO.insertC(UserSettings.toRow(createdUser.user.id, createdUser.user.settings))
        d <- userDetailsDAO.insertC(UserDetails.toRow(createdUser.user.id, createdUser.user.details))
      } yield User.fromRow(u, UserSettings.fromRow(s), UserDetails.fromRow(d))
      user <- userCreationAction.transact(dbTransactorProvider.transactor[F])
      _ <- registrationTokenDAO.delete(registrationTokenId)
    } yield user
  }

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    userDAO
      .delete(UserId.toDb(userId))
      .void

  def requestCreate[F[_]: Async: ContextShift](email: String): F[Unit] =
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
    } yield response

  // TODO: Add update function

}

object UserService {
  val registrationTokenLength: Natural = Natural(64)
  // TODO: Add proper sending address (via application conf)
  val londoSenderAddress: String = "noreply@londo.io"
}
