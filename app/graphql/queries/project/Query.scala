package graphql.queries.project

import cats.data.EitherT
import errors.ErrorContext
import errors.ServerError
import graphql.HasGraphQLServices.syntax._
import graphql.types.project.Project
import graphql.types.task.Task
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def fetchProject(input: FetchProjectInput): Future[Project] =
    withUserId { userId =>
      EitherT
        .fromOptionF(
          graphQLServices.projectService
            .get(
              userId,
              input.projectId.transformInto[db.ProjectId]
            ),
          ErrorContext.Project.NotFound.asServerError
        )
        .map(_.transformInto[Project])
        .value
        .handleServerError
    }

  @GraphQLField
  def fetchResolvedProject(input: FetchResolvedProjectInput): Future[ResolvedProject] = {
    withUserId { userId =>
      val projectId = input.projectId.transformInto[db.ProjectId]
      val transformer = for {
        project <- EitherT.fromOptionF(
          graphQLServices.projectService.get(userId, projectId),
          ErrorContext.Project.NotFound.asServerError
        )
        tasks <- EitherT.liftF[Future, ServerError, Seq[services.task.Task]](
          graphQLServices.taskService.all(userId, projectId)
        )
      } yield ResolvedProject(
        project.transformInto[Project],
        tasks.map(_.transformInto[Task])
      )

      transformer.value.handleServerError
    }
  }

  @GraphQLField
  def fetchAll: Future[Seq[Project]] =
    withUserId { userId =>
      graphQLServices.projectService
        .all(userId)
        .map(_.map(_.transformInto[Project]))
    }

}
