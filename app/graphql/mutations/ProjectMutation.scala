package graphql.mutations

import cats.data.NonEmptySet
import cats.effect.IO
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.project.{ Accessors, Project, ProjectCreation, ProjectId, ProjectUpdate }
import graphql.types.task.{ Task, TaskCreation, TaskKey }
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
      .map(_.fromInternal[Project])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def allowReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    // TODO: Check write access
    modifyAccessUsers(graphQLServices.projectService.allowReadUsers(_, _))(projectId, userIds)

  @GraphQLField
  def allowWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    // TODO: Check write access
    modifyAccessUsers(graphQLServices.projectService.allowWriteUsers(_, _))(projectId, userIds)

  @GraphQLField
  def blockReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    // TODO: Check write access
    modifyAccessUsers(graphQLServices.projectService.blockReadUsers(_, _))(projectId, userIds)

  @GraphQLField
  def blockWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    // TODO: Check write access
    modifyAccessUsers(graphQLServices.projectService.blockWriteUsers(_, _))(projectId, userIds)

  @GraphQLField
  def deleteProject(
      projectId: ProjectId
  ): Future[Project] =
    // TODO: Check write access
    graphQLServices.projectService
      .delete(projectId = projectId.toInternal)
      .map(_.fromInternal[Project])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def addPlainTask(
      projectId: ProjectId,
      plainCreation: TaskCreation.PlainCreation
  ): Future[Task.Plain] =
    // TODO: Check write access
    graphQLServices.taskService
      .createPlainTask(
        projectId.toInternal,
        plainCreation = plainCreation.toInternal
      )
      .map(_.fromInternal[Task.Plain])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def addProjectReferenceTask(
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReferenceCreation
  ): Future[Task.ProjectReference] =
    // TODO: Check write access
    graphQLServices.taskService
      .createProjectReferenceTask(
        projectId.toInternal,
        projectReferenceCreation = projectReferenceCreation.toInternal
      )
      .map(_.fromInternal[Task.ProjectReference])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def removePlainTask(
      taskKey: TaskKey
  ): Future[Task.Plain] =
    // TODO: Check write access
    graphQLServices.taskService
      .removePlainTask(taskKey.toInternal)
      .map(_.fromInternal[Task.Plain])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def removeProjectReferenceTask(
      taskKey: TaskKey
  ): Future[Task.ProjectReference] =
    // TODO: Check write access
    graphQLServices.taskService
      .removeProjectReferenceTask(taskKey.toInternal)
      .map(_.fromInternal[Task.ProjectReference])
      .unsafeToFuture()
      .handleServerError

  @GraphQLField
  def updateProject(
      projectId: ProjectId,
      projectUpdate: ProjectUpdate
  ): Future[Project] =
    // TODO: Check write access
    graphQLServices.projectService
      .update(
        projectId = projectId.toInternal,
        projectUpdate = projectUpdate.toInternal
      )
      .map(_.fromInternal[Project])
      .unsafeToFuture()
      .handleServerError

  private def modifyAccessUsers(
      serviceFunction: (
          services.project.ProjectId,
          NonEmptySet[services.user.UserId]
      ) => IO[ServerError.Valid[services.project.Accessors]]
  )(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    serviceFunction(
      projectId.toInternal,
      userIds.toInternal.toNes
    )
    // TODO: Flatten the two maps into one, if possible
      .map(_.map(a => services.project.Accessors.toRepresentation(a).fromInternal[Accessors]))
      .unsafeToFuture()
      .handleServerError

}
