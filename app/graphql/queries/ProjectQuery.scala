package graphql.queries

import cats.effect.IO
import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.project.{ Project, ProjectId }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait ProjectQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetchProject(projectId: ProjectId): Future[Project] = {
    allowedAccessViaError(
      graphQLServices.projectService
        .fetch[IO](projectId.toInternal)
    )(_.readAccessors.accessors, _.fromInternal[Project])
      .unsafeToFuture()
      .handleServerError
  }

}
