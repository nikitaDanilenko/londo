package graphql.queries

import cats.data.EitherT
import cats.effect.IO
import errors.ServerError
import graphql.types.FromInternal.syntax._
import graphql.types.project.{ Project, ProjectId }
import graphql.types.task.Progress
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import services.project.ResolvedProject

import scala.concurrent.Future

trait ProjectQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField()
  def fetchProject(projectId: ProjectId): Future[Project] = {
    validateProjectReadAccess(projectId)((_, project) => IO.pure(ServerError.result(project.fromInternal[Project])))
  }

  @GraphQLField
  def projectProgress(projectId: ProjectId): Future[Option[Progress]] =
    validateProjectReadAccess(projectId) { (_, project) =>
      EitherT(graphQLServices.projectService.resolveProject(project.id))
        .map(ResolvedProject.progress(_).map(_.fromInternal[Progress]))
        .value
    }

  private def validateProjectReadAccess[A](
      projectId: ProjectId
  )(
      f: (services.user.UserId, services.project.Project) => IO[ServerError.Or[A]]
  ): Future[A] = {
    validateProjectAccess(
      projectService = graphQLServices.projectService,
      projectId = projectId,
      accessorsOf = _.readAccessors.accessors
    )(f)
  }

}
