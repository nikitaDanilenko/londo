package graphql.queries

import cats.effect.IO
import db.keys.UserId
import graphql.types.user.User
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import java.util.UUID
import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetch(userId: UUID): Future[User] =
    validateAccess[IO](UserId(userId))
      .flatMap(graphQLServices.userService.fetch[IO])
      .map(User.fromInternal)
      .unsafeToFuture()

}
