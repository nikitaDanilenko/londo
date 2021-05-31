package graphql.mutations

import graphql.HasGraphQLServices.syntax._
import graphql.types.project.{ Project, ProjectCreation }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait ProjectMutation extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField
  def createProject(
      projectCreation: ProjectCreation
  ): Future[Project] =
    graphQLServices.projectService
      .create(ProjectCreation.toInternal(projectCreation))
      .unsafeToFuture()
      .handleServerError
      .map(Project.fromInternal)

}
