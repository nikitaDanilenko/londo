package graphql.queries

import cats.effect.IO
import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.user.{ User, UserId }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetchUser(userId: UserId): Future[User] =
    validateAccess[IO](userId)
      .flatMap(graphQLServices.userService.fetch[IO])
      .map(_.fromInternal[User])
      .unsafeToFuture()
      .handleServerError

}
