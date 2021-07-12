package graphql.mutations

import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.user.{ User, UserCreation }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait UserMutation extends HasGraphQLServices with HasLoggedInUser {
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
      .handleServerError

  @GraphQLField
  def createUser(userCreation: UserCreation): Future[User] =
    graphQLServices.userService
      .create(userCreation.toInternal)
      .map(_.fromInternal[User])
      .unsafeToFuture()
      .handleServerError

}
