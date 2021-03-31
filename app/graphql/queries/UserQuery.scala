package graphql.queries

import graphql.HasGraphQLServices
import sangria.macros.derive.GraphQLField
import services.user.{ User, UserId }

import java.util.UUID
import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices {
  import ioImplicits._

  @GraphQLField()
  def fetch(userId: UUID): Future[User] =
    graphQLServices.userService.fetch(UserId(userId)).unsafeToFuture()

}
