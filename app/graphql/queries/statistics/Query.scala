package graphql.queries.statistics

import cats.data.EitherT
import errors.{ ErrorContext, ServerError }
import graphql.HasGraphQLServices.syntax._
import graphql.queries.statistics.inputs.FetchDashboardAnalysisInput
import graphql.types.dashboard.Dashboard
import graphql.types.project.Project
import graphql.types.simulation.Simulation
import graphql.types.task.Task
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def fetchDashboardAnalysis(input: FetchDashboardAnalysisInput): Future[DashboardAnalysis] =
    withUserId { userId =>
      val dashboardId = input.dashboardId.transformInto[db.DashboardId]
      val transformer = for {
        dashboard <- EitherT.fromOptionF(
          graphQLServices.dashboardService.get(userId, dashboardId),
          ErrorContext.Dashboard.NotFound.asServerError
        )
        entries <- EitherT.liftF[Future, ServerError, Seq[services.dashboardEntry.DashboardEntry]](
          graphQLServices.dashboardEntryService.all(userId, dashboardId)
        )
        projectIds = entries.map(_.projectId)
        projects <- EitherT.liftF[Future, ServerError, Seq[services.project.Project]](
          graphQLServices.projectService.allOf(userId, projectIds)
        )
        tasksByProjectId <- EitherT.liftF[Future, ServerError, Map[db.ProjectId, Seq[services.task.Task]]](
          graphQLServices.taskService.allFor(userId, projectIds)
        )
        simulations <- EitherT.liftF[Future, ServerError, Map[db.TaskId, services.simulation.Simulation]](
          graphQLServices.simulationService.all(userId, dashboardId)
        )
      } yield {
        val resolvedProjects = projects.map(project =>
          ProjectAnalysis(
            project = project.transformInto[Project],
            tasks = tasksByProjectId.getOrElse(project.id, Seq.empty).map { task =>
              TaskAnalysis(
                task = task.transformInto[Task],
                simulation = simulations.get(task.id).map(_.transformInto[Simulation])
              )
            }
          )
        )
        val allTasks = resolvedProjects
          .flatMap(_.tasks)
          .map(resolvedTask =>
            processing.statistics.TaskWithSimulation(
              task = resolvedTask.task.transformInto[processing.statistics.Task],
              simulation = resolvedTask.simulation.map(_.reachedModifier)
            )
          )

        val dashboardStatistics = processing.statistics.StatisticsService.ofTasks(allTasks)

        DashboardAnalysis(
          dashboard.transformInto[Dashboard],
          resolvedProjects,
          dashboardStatistics.transformInto[DashboardStatistics]
        )
      }

      transformer.value.handleServerError
    }

}
