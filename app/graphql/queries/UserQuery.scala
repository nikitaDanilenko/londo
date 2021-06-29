package graphql.queries

import cats.effect.IO
import graphql.types.FromInternal.syntax._
import graphql.types.user.{ User, UserId }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import HasGraphQLServices.syntax._

import java.util.UUID
import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetch(userId: UUID): Future[User] =
    validateAccess[IO](UserId(userId))
      .flatMap(graphQLServices.userService.fetch[IO])
      .map(_.fromInternal[User])
      .unsafeToFuture()
      .handleServerError

}
