package graphql.mutations.project

import cats.data.EitherT
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.project.inputs._
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
  def createTask(
      input: CreateTaskInput
  ): Future[Task] =
    withUserId { userId =>
      EitherT(
        graphQLServices.taskService
          .create(
            userId = userId,
            projectId = input.projectId.transformInto[db.ProjectId],
            creation = input.taskCreation.transformInto[services.task.Creation]
          )
      ).map(_.transformInto[Task]).value.handleServerError
    }

  @GraphQLField
  def updateTask(
      input: UpdateTaskInput
  ): Future[Task] =
    withUserId { userId =>
      EitherT(
        graphQLServices.taskService
          .update(
            userId = userId,
            taskId = input.taskId.transformInto[db.TaskId],
            update = input.taskUpdate.transformInto[services.task.Update]
          )
      )
        .map(_.transformInto[Task])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteTask(
      input: DeleteTaskInput
  ): Future[Boolean] =
    withUserId { userId =>
      EitherT(
        graphQLServices.taskService
          .delete(
            userId,
            input.taskId.transformInto[db.TaskId]
          )
      ).value.handleServerError
    }

}
