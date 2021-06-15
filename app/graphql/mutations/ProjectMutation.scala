package graphql.mutations

import graphql.HasGraphQLServices.syntax._
import graphql.types.project.{ Accessors, Project, ProjectCreation, ProjectId }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import services.project.{ AccessFromDB, AccessKind, ProjectAccess }
import services.project.AccessFromDB.instances._
import services.project.ProjectAccess.DbRepresentation

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

  @GraphQLField
  def setReadAccess(
      projectId: ProjectId,
      accessors: Accessors
  ): Future[Accessors] =
    graphQLServices.projectService
      .setReadAccess(
        ProjectId.toInternal(projectId),
        toServiceAccess[AccessKind.Read](accessors)
      )
      .unsafeToFuture()
      .map(fromServiceAccess(_))

  @GraphQLField
  def setWriteAccess(
      projectId: ProjectId,
      accessors: Accessors
  ): Future[Accessors] =
    graphQLServices.projectService
      .setWriteAccess(
        ProjectId.toInternal(projectId),
        toServiceAccess[AccessKind.Write](accessors)
      )
      .unsafeToFuture()
      .map(fromServiceAccess(_))

  private def toServiceAccess[AK](accessors: Accessors): ProjectAccess[AK] =
    ProjectAccess[AK](services.project.Accessors.fromRepresentation(Accessors.toInternal(accessors)))

  private def fromServiceAccess[AccessK, DBAccessK, DBAccessEntry](
      dbRepresentation: DbRepresentation[DBAccessK, DBAccessEntry]
  )(implicit accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]): Accessors =
    Accessors.fromInternal(
      services.project.Accessors.toRepresentation(ProjectAccess.fromDb(dbRepresentation).accessors)
    )

}
