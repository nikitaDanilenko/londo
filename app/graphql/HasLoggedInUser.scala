package graphql

import cats.syntax.flatMap._
import cats.{ ApplicativeThrow, MonadThrow }
import errors.{ ErrorContext, ServerException }
import io.scalaland.chimney.dsl._
import security.jwt.LoggedIn
import utils.transformer.implicits._

import scala.util.chaining._

trait HasLoggedInUser {
  protected def loggedIn: Option[LoggedIn]

  final protected def withUser[F[_]: MonadThrow, A](action: LoggedIn => F[A]): F[A] =
    ApplicativeThrow[F]
      .fromOption(
        loggedIn,
        ServerException(ErrorContext.Authentication.Token.Restricted.asServerError)
      )
      .flatMap(action)

  final protected def withUserId[F[_]: MonadThrow, A](action: db.UserId => F[A]): F[A] =
    withUser(_.userId.transformInto[db.UserId].pipe(action))

}
