package graphql.queries

import db.models.User
import graphql.HasGraphQLServices
import sangria.macros.derive.GraphQLField
import services.user.UserId

import java.util.UUID
import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices {
  import ioImplicits._

  @GraphQLField
  def userById(userId: UUID): Future[Option[User]] = {
    graphQLServices.userDTO.find(UserId(userId)).unsafeToFuture()
  }

}
