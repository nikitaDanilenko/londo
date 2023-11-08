package graphql.mutations.dashboard

import cats.data.EitherT
import graphql.HasGraphQLServices.syntax._
import graphql.mutations.dashboard.inputs._
import graphql.queries.statistics.TaskAnalysis
import graphql.types.dashboard.Dashboard
import graphql.types.dashboardEntry.DashboardEntry
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl.TransformerOps
import sangria.macros.derive.GraphQLField
import services.task.Progress
import utils.math.MathUtil

import scala.concurrent.Future

trait Mutation extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def createDashboard(input: CreateDashboardInput): Future[Dashboard] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardService
          .create(
            ownerId = userId,
            creation = input.dashboardCreation.transformInto[services.dashboard.Creation]
          )
      )
        .map(_.transformInto[Dashboard])
        .value
        .handleServerError
    }

  @GraphQLField
  def updateDashboard(input: UpdateDashboardInput): Future[Dashboard] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardService
          .update(
            ownerId = userId,
            id = input.dashboardId.transformInto[db.DashboardId],
            update = input.dashboardUpdate.transformInto[services.dashboard.Update]
          )
      )
        .map(_.transformInto[Dashboard])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteDashboard(input: DeleteDashboardInput): Future[Boolean] =
    withUserId { userId =>
      graphQLServices.dashboardService
        .delete(
          ownerId = userId,
          id = input.dashboardId.transformInto[db.DashboardId]
        )
        .handleServerError
    }

  @GraphQLField
  def createDashboardEntry(input: CreateDashboardEntryInput): Future[DashboardEntry] =
    withUserId { userId =>
      EitherT(
        graphQLServices.dashboardEntryService
          .create(
            userId = userId,
            dashboardId = input.dashboardId.transformInto[db.DashboardId],
            creation = input.dashboardEntryCreation.transformInto[services.dashboardEntry.Creation]
          )
      )
        .map(_.transformInto[DashboardEntry])
        .value
        .handleServerError
    }

  @GraphQLField
  def deleteDashboardEntry(input: DeleteDashboardEntryInput): Future[Boolean] =
    withUserId { userId =>
      graphQLServices.dashboardEntryService
        .delete(
          userId = userId,
          key = db.daos.dashboardEntry.DashboardEntryKey(
            input.dashboardId.transformInto[db.DashboardId],
            input.projectId.transformInto[db.ProjectId]
          )
        )
        .handleServerError
    }

  @GraphQLField
  def updateTaskWithSimulation(input: UpdateTaskWithSimulationInput): Future[TaskAnalysis] =
    withUserId { userId =>
      // TODO #29: Make this update transactional
      val transformer =
        for {
          task <- EitherT(
            graphQLServices.taskService
              .update(
                userId = userId,
                taskId = input.taskId.transformInto[db.TaskId],
                update = input.taskUpdate.transformInto[services.task.Update]
              )
          )
          simulation <-
            input.simulation.fold {
              EitherT(
                graphQLServices.simulationService
                  .delete(
                    userId = userId,
                    dashboardId = input.dashboardId.transformInto[db.DashboardId],
                    taskId = input.taskId.transformInto[db.TaskId]
                  )
              )
                .map(_ => Option.empty[services.simulation.Simulation])
            } { simulation =>
              EitherT(
                graphQLServices.simulationService
                  .upsert(
                    userId = userId,
                    dashboardId = input.dashboardId.transformInto[db.DashboardId],
                    taskId = input.taskId.transformInto[db.TaskId],
                    simulation = simulation.transformInto[services.simulation.IncomingSimulation]
                  )
              ).map(Some(_))
            }
        } yield {
          val incompleteTaskStatistics = Option.when(Progress.isComplete(task.progress))(
            processing.statistics.task.StatisticsService.incompleteOfTask(
              processing.statistics.TaskWithSimulation(
                task.transformInto[processing.statistics.Task],
                simulation.map(_.reachedModifier)
              ),
              numberOfTasks = input.numberOfTotalTasks.map(_.transformInto[math.Positive]),
              numberOfcountingTasks = input.numberOfcountingTasks.map(_.transformInto[math.Positive])
            )
          )
          TaskAnalysis.from(
            task = task,
            simulation = simulation,
            incompleteStatistics = incompleteTaskStatistics,
            numberOfDecimalPlaces = input.numberOfDecimalPlaces.transformInto[math.Positive]
          )
        }

      transformer.value.handleServerError
    }

}
