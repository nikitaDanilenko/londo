package graphql.queries

import cats.data.{ EitherT, NonEmptySet }
import cats.effect.IO
import errors.ServerError
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.project.{ Project, ProjectId }
import graphql.types.task.Progress
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import services.access.Accessors
import services.project.ResolvedProject

import scala.concurrent.Future

trait ProjectQuery extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField
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

  @GraphQLField
  def fetchOwn: Future[Seq[Project]] =
    withUser(userId =>
      graphQLServices.projectService
        .fetchOwn(userId.toInternal)
        .map(_.map(_.fromInternal[Project]))
    )
      .unsafeToFuture()

  @GraphQLField
  def fetchWithReadAccess: Future[Seq[Project]] =
    withUser(userId =>
      graphQLServices.projectService
        .fetchWithReadAccess(userId.toInternal)
        .map(_.map(_.fromInternal[Project]))
    )
      .unsafeToFuture()

  @GraphQLField
  def fetchWithWriteAccess: Future[Seq[Project]] =
    withUser(userId =>
      graphQLServices.projectService
        .fetchWithReadAccess(userId.toInternal)
        .map(_.map(_.fromInternal[Project]))
    )
      .unsafeToFuture()

  private def validateProjectReadAccess[A](
      projectId: ProjectId
  )(
      f: (services.user.UserId, services.project.Project) => IO[ServerError.Or[A]]
  ): Future[A] = {
    validateProjectAccess(
      projectService = graphQLServices.projectService,
      projectId = projectId,
      accessorsOf = p => Accessors.allowUsers(p.readAccessors.accessors, NonEmptySet.of(p.ownerId))
    )(f)
  }

}
