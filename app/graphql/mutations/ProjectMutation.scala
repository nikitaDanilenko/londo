package graphql.mutations

import cats.data.NonEmptySet
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.project.{ Accessors, Project, ProjectCreation, ProjectId, ProjectUpdate }
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
      .create(projectCreation.toInternal)
      .unsafeToFuture()
      .handleServerError
      .map(_.fromInternal)

  @GraphQLField
  def allowReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.allowReadUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def allowWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.allowWriteUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def blockReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.blockReadUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def blockWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    modifyAccessUsers(graphQLServices.projectService.blockWriteUsers(_, _).unsafeToFuture())(projectId, userIds)

  @GraphQLField
  def deleteProject(
      projectId: ProjectId
  ): Future[Project] =
    graphQLServices.projectService
      .delete(projectId = projectId.toInternal)
      .unsafeToFuture()
      .handleServerError
      .map(_.fromInternal)

  def updateProject(
      projectId: ProjectId,
      projectUpdate: ProjectUpdate
  ): Future[Project] =
    graphQLServices.projectService
      .update(
        projectId = projectId.toInternal,
        projectUpdate = projectUpdate.toInternal
      )
      .unsafeToFuture()
      .handleServerError
      .map(_.fromInternal)

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
      projectId.toInternal,
      userIds.toInternal.toNes
    ).handleServerError
      .map(accessors => services.project.Accessors.toRepresentation(accessors).fromInternal)

}
