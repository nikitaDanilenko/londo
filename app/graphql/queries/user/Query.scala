package graphql.queries.user

import cats.data.EitherT
import errors.ErrorContext
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.user.User
import graphql.types.user.UserId
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl.TransformerOps
import sangria.macros.derive.GraphQLField
import utils.transformer.implicits._

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField()
  def fetchUser: Future[User] =
    withUser { loggedIn =>
      EitherT
        .fromOptionF(
          graphQLServices.userService
            .get(loggedIn.userId.transformInto[db.UserId]),
          ErrorContext.User.NotFound.asServerError
        )
        .map(_.transformInto[User])
        .value
        .handleServerError
    }

  @GraphQLField
  def findUser(input: FindUserInput): Future[Seq[FindUserResult]] =
    for {
      all <- graphQLServices.userService.getByIdentifier(input.searchString)
    } yield all.map(user =>
      FindUserResult(
        user.id.transformInto[UserId],
        user.nickname
      )
    )

}
