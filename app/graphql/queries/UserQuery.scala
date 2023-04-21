package graphql.queries

import cats.data.EitherT
import errors.ErrorContext
import graphql.HasGraphQLServices.syntax._
import graphql.types.user.User
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl.TransformerOps
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait UserQuery extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField()
  def fetchUser: Future[User] =
    withUser { userId =>
      EitherT
        .fromOptionF(
          graphQLServices.userService
            .get(userId.transformInto[db.UserId]),
          ErrorContext.User.NotFound.asServerError
        )
        .map(_.transformInto[User])
        .value
        .handleServerError
    }

}
