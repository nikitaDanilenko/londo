package graphql

import cats.syntax.flatMap._
import cats.{ ApplicativeThrow, MonadThrow }
import errors.{ ErrorContext, ServerException }
import security.jwt.LoggedIn

trait HasLoggedInUser {
  protected def loggedIn: Option[LoggedIn]

  final protected def withUser[F[_]: MonadThrow, A](action: LoggedIn => F[A]): F[A] =
    ApplicativeThrow[F]
      .fromOption(
        loggedIn,
        ServerException(ErrorContext.Authentication.Token.Restricted.asServerError)
      )
      .flatMap(action)

}
