package graphql.mutations

import cats.data.NonEmptySet
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.project.{ Accessors, Project, ProjectCreation, ProjectId }
import graphql.types.user.UserId
import graphql.types.util.NonEmptyList
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

  @GraphQLField
  def allowReadUsers(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.allowReadUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def allowWriteUsers(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.allowWriteUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def blockReadUsers(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.blockReadUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def blockWriteUsers(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.blockWriteUsers(_, _).unsafeToFuture())(projectId, userIds)

  private def modifyAccessUsers(
      serviceFunction: (
          services.project.ProjectId,
          NonEmptySet[services.user.UserId]
      ) => Future[ServerError.Valid[services.project.Accessors]]
  )(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    serviceFunction(
      ProjectId.toInternal(projectId),
      NonEmptyList.toInternal(userIds).map(UserId.toInternal).toNes
    ).handleServerError
      .map(accessors => Accessors.fromInternal(services.project.Accessors.toRepresentation(accessors)))

}
