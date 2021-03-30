package graphql.mutations

import graphql.HasGraphQLServices
import graphql.HasGraphQLServices.syntax._
import sangria.macros.derive.GraphQLField
import services.user.{ User, UserCreation }

import scala.concurrent.Future

trait UserMutation extends HasGraphQLServices {
  import ioImplicits._

  @GraphQLField
  def login(
      nickname: String,
      password: String,
      publicSignatureKey: String,
      isValidityUnrestricted: Boolean
  ): Future[String] =
    graphQLServices.userService
      .login(nickname, password, publicSignatureKey, isValidityUnrestricted)
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def requestCreate(email: String): Future[Unit] =
    graphQLServices.userService
      .requestCreate(email)
      .unsafeToFuture()

  @GraphQLField
  def createUser(userCreation: UserCreation): Future[User] =
    graphQLServices.userService
      .create(userCreation)
      .unsafeToFuture()

}
