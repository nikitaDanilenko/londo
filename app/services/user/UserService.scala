package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift }
import cats.syntax.applicative._
import cats.syntax.contravariantSemigroupal._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.DbTransactorProvider
import db.generated.daos.{ RegistrationTokenDAO, SessionKeyDAO, UserDAO, UserDetailsDAO, UserSettingsDAO }
import db.models.{ RegistrationToken, SessionKey }
import doobie.syntax.connectionio._
import errors.{ ServerError, ServerException }
import security.Hash
import services.email.{ EmailParameters, EmailService }
import spire.math.Natural
import utils.random.RandomGenerator

import javax.inject.Inject

class UserService @Inject() (
    userDAO: UserDAO,
    userSettingsDAO: UserSettingsDAO,
    userDetailsDAO: UserDetailsDAO,
    sessionKeyDAO: SessionKeyDAO,
    registrationTokenDAO: RegistrationTokenDAO,
    emailService: EmailService,
    dbTransactorProvider: DbTransactorProvider
) {

  def login[F[_]: Async: ContextShift](
      nickname: String,
      password: String,
      publicSignatureKey: String
  ): F[ServerError.Or[User]] = {
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
          .flatMap(_ => fetch(user).map(_.asRight[ServerError]))
      else (ServerError.Login.Failure: ServerError).asLeft[User].pure
    }.value
  }

  def fetch[F[_]: Async: ContextShift](user: db.models.User): F[User] =
    (
      userSettingsDAO.find(user.id).map(_.fold(UserSettings.default)(UserSettings.fromRow)),
      userDetailsDAO.find(user.id).map(_.fold(UserDetails.default)(UserDetails.fromRow))
    ).mapN(User.fromRow(user, _, _))

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
      userCreation = for {
        u <- userDAO.insertF(User.toRow(createdUser.user, createdUser.passwordParameters))
        s <- userSettingsDAO.insertF(UserSettings.toRow(createdUser.user.id, createdUser.user.settings))
        d <- userDetailsDAO.insertF(UserDetails.toRow(createdUser.user.id, createdUser.user.details))
      } yield User.fromRow(u, UserSettings.fromRow(s), UserDetails.fromRow(d))
      user <- userCreation.transact(dbTransactorProvider.transactor[F])
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
