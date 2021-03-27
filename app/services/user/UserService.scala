package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.generated.daos.{ SessionKeyDAO, UserDAO, UserDetailsDAO, UserSettingsDAO }
import db.models.SessionKey
import errors.ServerError
import security.Hash

import javax.inject.Inject

class UserService @Inject() (
    userDAO: UserDAO,
    userSettingsDAO: UserSettingsDAO,
    userDetailsDAO: UserDetailsDAO,
    sessionKeyDAO: SessionKeyDAO
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
      val isValid = Hash.verify(password, user.passwordSalt, user.passwordHash)
      if (isValid)
        sessionKeyDAO
          .replace(SessionKey(user.id, publicSignatureKey))
          .flatMap(_ => fetch(user).map(_.asRight[ServerError]))
      else Async[F].pure((ServerError.Login.Failure: ServerError).asLeft[User])
    }.value
  }

  def fetch[F[_]: Async: ContextShift](user: db.models.User): F[User] =
    (
      userDetailsDAO.find(user.id).map(_.fold(UserDetails.default)(UserDetails.fromRow)),
      userSettingsDAO.find(user.id).map(_.fold(UserSettings.default)(UserSettings.fromRow))
    ).mapN((details, settings) =>
      User(
        id = UserId(user.id),
        nickname = user.nickname,
        email = user.nickname,
        passwordSalt = user.passwordSalt,
        passwordHash = user.passwordHash,
        settings = settings,
        details = details
      )
    )

  def logout[F[_]: Async: ContextShift](userId: UserId): F[Boolean] = ???

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[User] = ???

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Boolean] = ???

  // TODO: Add update function

}
