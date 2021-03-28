package graphql.mutations

import graphql.HasGraphQLServices
import sangria.macros.derive.GraphQLField
import services.user.User

import scala.concurrent.Future

trait UserMutation extends HasGraphQLServices {
  import ioImplicits._

  @GraphQLField
  def login(nickname: String, password: String, publicSignatureKey: String): Future[User] = {
    graphQLServices.userService.login(nickname, password, publicSignatureKey).unsafeToFuture().flatMap {
      _.fold(
        error => Future.failed(new Throwable(error.message)),
        Future.successful
      )
    }
  }

}
