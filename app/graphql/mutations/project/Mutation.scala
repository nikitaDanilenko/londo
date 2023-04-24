package graphql.mutations.project

import cats.data.EitherT
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.project.inputs.{ CreateProjectInput, DeleteProjectInput, UpdateProjectInput }
import graphql.types.project._
import graphql.types.task._
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Mutation extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def createProject(
      input: CreateProjectInput
  ): Future[Project] =
    withUserId { userId =>
      EitherT(
        graphQLServices.projectService
          .create(
            userId = userId,
            creation = input.transformInto[services.project.Creation]
          )
      )
        .map(_.transformInto[Project])
        .value
        .handleServerError
    }

  @GraphQLField
  def updateProject(
      input: UpdateProjectInput
  ): Future[Project] =
    withUserId { userId =>
      EitherT(
        graphQLServices.projectService
          .update(
            userId = userId,
            // TODO: Consider nesting, i.e. use one type for the update, and one type for the input to avoid the somewhat awkward transformation below.
            projectId = input.projectId.transformInto[db.ProjectId],
            update = input.transformInto[services.project.Update]
          )
      )
        .map(_.transformInto[Project])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteProject(
      input: DeleteProjectInput
  ): Future[Boolean] =
    withUserId { userId =>
      EitherT(
        graphQLServices.projectService
          .delete(
            userId = userId,
            id = input.projectId.transformInto[db.ProjectId]
          )
      ).value.handleServerError
    }

  @GraphQLField
  def addTask(
      projectId: ProjectId,
      creation: TaskCreation
  ): Future[Task] = ???
//    validateProjectWriteAccess(projectId) { _ =>
//      graphQLServices.taskService
//        .createPlainTask(
//          projectId.toInternal,
//          plainCreation = plainCreation.toInternal
//        )
//        .map(_.fromInternal[Task.Plain])
//    }

  @GraphQLField
  def removeTask(
      projectId: ProjectId,
      taskId: TaskId
  ): Future[Task] = ???
//    validateProjectWriteAccess(taskKey.projectId) { _ =>
//      graphQLServices.taskService
//        .removePlainTask(taskKey.toInternal)
//        .map(_.fromInternal[Task.Plain])
//    }

  @GraphQLField
  def updateTask(
      projectId: ProjectId,
      taskId: TaskId,
      taskUpdate: TaskUpdate
  ): Future[Task] = ???
//    validateProjectWriteAccess(taskKey.projectId) { _ =>
//      graphQLServices.taskService
//        .updatePlainTask(taskKey.toInternal, plainUpdate.toInternal)
//        .map(_.fromInternal[Task.Plain])
//    }

  def updateTaskProgress(
      projectId: ProjectId,
      taskId: TaskId,
      progressUpdate: ProgressUpdate
  ): Future[Task] = ???
//    validateProjectWriteAccess(taskKey.projectId) { _ =>
//      graphQLServices.taskService
//        .updateTaskProgress(taskKey.toInternal, progressUpdate.toInternal)
//        .map(_.fromInternal[Task.Plain])

}
