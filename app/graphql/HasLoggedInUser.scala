package graphql

import cats.ApplicativeThrow
import errors.{ ServerError, ServerException }
import graphql.types.user.UserId

trait HasLoggedInUser {
  protected def loggedInUserId: Option[UserId]

  final protected def validateAccess[F[_]: ApplicativeThrow](accessedUserId: UserId): F[services.user.UserId] =
    ApplicativeThrow[F].fromEither(
      loggedInUserId
        .filter(_ == accessedUserId)
        .toRight(ServerException(ServerError.Authentication.Token.Restricted))
        .map(UserId.toInternal)
    )

}
