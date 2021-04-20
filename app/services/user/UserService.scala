package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.DbTransactorProvider
import db.generated.daos._
import db.keys.UserId
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

import java.util.UUID
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
            Async[F].liftIO(generateUserAuthentication(user.id, isValidityUnrestricted).map(_.asRight[ServerError]))
          )
      else (ServerError.Login.Failure: ServerError).asLeft[String].pure
    }.value
  }

  def generateUserAuthentication(userId: UUID, isValidityUnrestricted: Boolean): IO[String] = {
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
    for {
      userRow <- userDAO.find(userId.uuid).flatMap(Async[F].fromOption(_, ServerException(ServerError.User.NotFound)))
      userSettings <- userSettingsDAO.find(userId.uuid)
      userDetails <- userDetailsDAO.find(userId.uuid)
    } yield User.fromRow(
      userRow = userRow,
      settings = userSettings.fold(UserSettings.default)(UserSettings.fromRow),
      details = userDetails.fold(UserDetails.default)(UserDetails.fromRow)
    )
  }

  def logout[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    sessionKeyDAO
      .delete(userId.uuid)
      .void

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[User] = {
    for {
      localToken <- registrationTokenDAO.find(userCreation.email)
      _ <- Async[F].fromOption(
        localToken.filter(_.token == userCreation.token),
        ServerException(ServerError.Authentication.Token.Registration)
      )
      createdUser <- Async[F].liftIO(UserCreation.create(userCreation))
      userCreationAction = for {
        u <- userDAO.insertF(User.toRow(createdUser.user, createdUser.passwordParameters))
        s <- userSettingsDAO.insertF(UserSettings.toRow(createdUser.user.id, createdUser.user.settings))
        d <- userDetailsDAO.insertF(UserDetails.toRow(createdUser.user.id, createdUser.user.details))
      } yield User.fromRow(u, UserSettings.fromRow(s), UserDetails.fromRow(d))
      user <- userCreationAction.transact(dbTransactorProvider.transactor[F])
      _ <- registrationTokenDAO.delete(userCreation.email)
    } yield user
  }

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    userDAO
      .delete(userId.uuid)
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
