package graphql.mutations

import graphql.HasGraphQLServices
import graphql.HasGraphQLServices.syntax._
import sangria.macros.derive.GraphQLField
import services.user.User

import scala.concurrent.Future

trait UserMutation extends HasGraphQLServices {
  import ioImplicits._

  @GraphQLField
  def login(nickname: String, password: String, publicSignatureKey: String): Future[User] = {
    graphQLServices.userService
      .login(nickname, password, publicSignatureKey)
      .unsafeToFuture()
      .handleServerError
  }

}
