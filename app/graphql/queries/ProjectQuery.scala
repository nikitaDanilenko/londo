package graphql.queries

import cats.effect.IO
import errors.ServerError
import graphql.types.FromInternal.syntax._
import graphql.types.project.{ Project, ProjectId }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait ProjectQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetchProject(projectId: ProjectId): Future[Project] = {
    validateProjectReadAccess(projectId)((_, project) => IO.pure(ServerError.valid(project.fromInternal[Project])))
  }

  private def validateProjectReadAccess[A](
      projectId: ProjectId
  )(
      f: (services.user.UserId, services.project.Project) => IO[ServerError.Valid[A]]
  ): Future[A] = {
    validateProjectProjectAccess(
      projectService = graphQLServices.projectService,
      projectId = projectId,
      accessorsOf = _.readAccessors.accessors
    )(f)
  }

}
