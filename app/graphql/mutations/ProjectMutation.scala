package graphql.mutations

import cats.data.{ EitherT, NonEmptySet }
import cats.effect.IO
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.access.Accessors
import graphql.types.project._
import graphql.types.task._
import graphql.types.user.UserId
import graphql.types.util.NonEmptyList
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import services.access

import scala.concurrent.Future

trait ProjectMutation extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  @GraphQLField
  def createProject(
      projectCreation: ProjectCreation
  ): Future[Project] = {
    withUser { userId =>
      graphQLServices.projectService
        .create(userId.toInternal, projectCreation.toInternal)
    }
      .map(_.fromInternal[Project])
      .unsafeToFuture()
      .handleServerError
  }

  @GraphQLField
  def allowReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    validateProjectWriteAccess(projectId) { _ =>
      modifyAccessUsers(graphQLServices.projectService.allowReadUsers(_, _))(projectId, userIds)
    }

  @GraphQLField
  def allowWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    validateProjectWriteAccess(projectId) { _ =>
      modifyAccessUsers(graphQLServices.projectService.allowWriteUsers(_, _))(projectId, userIds)
    }

  @GraphQLField
  def blockReadUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    validateProjectWriteAccess(projectId) { _ =>
      modifyAccessUsers(graphQLServices.projectService.blockReadUsers(_, _))(projectId, userIds)
    }

  @GraphQLField
  def blockWriteUsersProject(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): Future[Accessors] =
    validateProjectWriteAccess(projectId) { _ =>
      modifyAccessUsers(graphQLServices.projectService.blockWriteUsers(_, _))(projectId, userIds)
    }

  @GraphQLField
  def deleteProject(
      projectId: ProjectId
  ): Future[Project] =
    validateProjectWriteAccess(projectId) { _ =>
      graphQLServices.projectService
        .delete(projectId = projectId.toInternal)
        .map(_.fromInternal[Project])
    }

  @GraphQLField
  def addPlainTask(
      projectId: ProjectId,
      plainCreation: TaskCreation.PlainCreation
  ): Future[Task.Plain] =
    validateProjectWriteAccess(projectId) { _ =>
      graphQLServices.taskService
        .createPlainTask(
          projectId.toInternal,
          plainCreation = plainCreation.toInternal
        )
        .map(_.fromInternal[Task.Plain])
    }

  @GraphQLField
  def addProjectReferenceTask(
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReferenceCreation
  ): Future[Task.ProjectReference] =
    validateProjectWriteAccess(projectId) { _ =>
      graphQLServices.taskService
        .createProjectReferenceTask(
          projectId.toInternal,
          projectReferenceCreation = projectReferenceCreation.toInternal
        )
        .map(_.fromInternal[Task.ProjectReference])
    }

  @GraphQLField
  def removePlainTask(
      taskKey: TaskKey
  ): Future[Task.Plain] =
    validateProjectWriteAccess(taskKey.projectId) { _ =>
      graphQLServices.taskService
        .removePlainTask(taskKey.toInternal)
        .map(_.fromInternal[Task.Plain])
    }

  @GraphQLField
  def removeProjectReferenceTask(
      taskKey: TaskKey
  ): Future[Task.ProjectReference] =
    validateProjectWriteAccess(taskKey.projectId) { _ =>
      graphQLServices.taskService
        .removeProjectReferenceTask(taskKey.toInternal)
        .map(_.fromInternal[Task.ProjectReference])
    }

  @GraphQLField
  def updateProject(
      projectId: ProjectId,
      projectUpdate: ProjectUpdate
  ): Future[Project] =
    validateProjectWriteAccess(projectId) { _ =>
      graphQLServices.projectService
        .update(
          projectId = projectId.toInternal,
          projectUpdate = projectUpdate.toInternal
        )
        .map(_.fromInternal[Project])
    }

  @GraphQLField
  def updatePlainTask(taskKey: TaskKey, plainUpdate: TaskUpdate.PlainUpdate): Future[Task.Plain] =
    validateProjectWriteAccess(taskKey.projectId) { _ =>
      graphQLServices.taskService
        .updatePlainTask(taskKey.toInternal, plainUpdate.toInternal)
        .map(_.fromInternal[Task.Plain])
    }

  @GraphQLField
  def updateProjectReferenceTask(
      taskKey: TaskKey,
      projectReferenceUpdate: TaskUpdate.ProjectReferenceUpdate
  ): Future[Task.ProjectReference] =
    validateProjectWriteAccess(taskKey.projectId) { _ =>
      graphQLServices.taskService
        .updateProjectReferenceTask(taskKey.toInternal, projectReferenceUpdate.toInternal)
        .map(_.fromInternal[Task.ProjectReference])
    }

  def updateTaskProgress(taskKey: TaskKey, progressUpdate: ProgressUpdate): Future[Task.Plain] =
    validateProjectWriteAccess(taskKey.projectId) { _ =>
      graphQLServices.taskService
        .updateTaskProgress(taskKey.toInternal, progressUpdate.toInternal)
        .map(_.fromInternal[Task.Plain])
    }

  private def modifyAccessUsers(
      serviceFunction: (
          services.project.ProjectId,
          NonEmptySet[services.user.UserId]
      ) => IO[ServerError.Valid[access.Accessors]]
  )(
      projectId: ProjectId,
      userIds: NonEmptyList[UserId]
  ): IO[ServerError.Valid[Accessors]] =
    EitherT(
      serviceFunction(
        projectId.toInternal,
        userIds.toInternal.toNes
      ).map(_.toEither)
    )
      .map(a => services.access.Accessors.toRepresentation(a).fromInternal[Accessors])
      .value
      .map(ServerError.fromEitherNel)

  private def validateProjectWriteAccess[A](
      projectId: ProjectId
  )(
      f: services.user.UserId => IO[ServerError.Valid[A]]
  ): Future[A] = {
    validateProjectProjectAccess(
      projectService = graphQLServices.projectService,
      projectId = projectId,
      accessorsOf = _.writeAccessors.accessors
    )((user, _) => f(user))
  }

}
