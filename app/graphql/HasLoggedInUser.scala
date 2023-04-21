package graphql

import cats.syntax.flatMap._
import cats.{ ApplicativeThrow, MonadThrow }
import errors.{ ErrorContext, ServerException }
import graphql.types.user.UserId
import security.jwt.JwtContent

trait HasLoggedInUser {
  protected def loggedInJwtContent: Option[JwtContent]

  final protected def withUser[F[_]: MonadThrow, A](create: UserId => F[A]): F[A] =
    ApplicativeThrow[F]
      .fromOption(
        loggedInJwtContent,
        ServerException(ErrorContext.Authentication.Token.Restricted.asServerError)
      )
      .flatMap(jwtContent => create(jwtContent.userId))

}
