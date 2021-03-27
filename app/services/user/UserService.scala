package services.user

import cats.data.EitherT
import cats.effect.{ Async, ContextShift }
import cats.syntax.applicative._
import cats.syntax.contravariantSemigroupal._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import db.DbTransactorProvider
import db.generated.daos.{ SessionKeyDAO, UserDAO, UserDetailsDAO, UserSettingsDAO }
import db.models.SessionKey
import doobie.syntax.connectionio._
import errors.ServerError
import security.Hash

import javax.inject.Inject

class UserService @Inject() (
    userDAO: UserDAO,
    userSettingsDAO: UserSettingsDAO,
    userDetailsDAO: UserDetailsDAO,
    sessionKeyDAO: SessionKeyDAO,
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
      val isValid = Hash.verify(password, user.passwordSalt, user.passwordHash)
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

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[User] =
    Async[F].liftIO(UserCreation.create(userCreation)).flatMap { user =>
      val c = for {
        u <- userDAO.insertF(User.toRow(user))
        s <- userSettingsDAO.insertF(UserSettings.toRow(user.id, user.settings))
        d <- userDetailsDAO.insertF(UserDetails.toRow(user.id, user.details))
      } yield User.fromRow(u, UserSettings.fromRow(s), UserDetails.fromRow(d))
      c.transact(dbTransactorProvider.transactor[F])
    }

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Unit] =
    userDAO
      .delete(userId.uuid)
      .void

  // TODO: Add update function

}
