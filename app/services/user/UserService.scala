package services.user

import cats.effect.{ Async, ContextShift }
import db.generated.daos.UserDAO

import javax.inject.Inject

class UserService @Inject() (userDAO: UserDAO) {

  def login[F[_]: Async: ContextShift](
      userId: UserId,
      password: String,
      publicSignatureKey: String
  ): F[Option[String]] = ???

  def logout[F[_]: Async: ContextShift](userId: UserId): F[Boolean] = ???

  def create[F[_]: Async: ContextShift](userCreation: UserCreation): F[User] = ???

  def delete[F[_]: Async: ContextShift](userId: UserId): F[Boolean] = ???

  // TODO: Add update function

}
