package graphql

import cats.ApplicativeThrow
import db.keys.UserId
import errors.{ ServerError, ServerException }

trait HasLoggedInUser {
  protected def loggedInUserId: Option[UserId]

  final protected def validateAccess[F[_]: ApplicativeThrow](accessedUserId: UserId): F[UserId] =
    ApplicativeThrow[F].fromEither(
      loggedInUserId
        .filter(_ == accessedUserId)
        .toRight(ServerException(ServerError.Authentication.Token.Restricted))
    )

}
