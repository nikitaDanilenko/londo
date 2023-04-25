package graphql.queries.project

import cats.data.EitherT
import errors.ErrorContext
import graphql.HasGraphQLServices.syntax._
import graphql.types.project.Project
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def fetchProject(input: FetchProjectInput): Future[Project] =
    withUserId { userId =>
      EitherT
        .fromOptionF(
          graphQLServices.projectService
            .get(
              userId,
              input.projectId.transformInto[db.ProjectId]
            ),
          ErrorContext.Project.NotFound.asServerError
        )
        .map(_.transformInto[Project])
        .value
        .handleServerError
    }

  @GraphQLField
  def fetchAll: Future[Seq[Project]] =
    withUserId { userId =>
      graphQLServices.projectService
        .all(userId)
        .map(_.map(_.transformInto[Project]))
    }

}
