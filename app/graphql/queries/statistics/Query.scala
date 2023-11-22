package graphql.queries.statistics

import cats.data.EitherT
import errors.{ ErrorContext, ServerError }
import graphql.HasGraphQLServices.syntax._
import graphql.queries.statistics.inputs.FetchDashboardAnalysisInput
import graphql.types.dashboard.Dashboard
import graphql.types.project.Project
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import math.Positive
import processing.statistics.TaskWithSimulation
import processing.statistics.dashboard.StatisticsService
import sangria.macros.derive.GraphQLField
import services.task.Progress
import spire.math.Natural

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
        analysis <- fetchAnalysis(
          dashboard = dashboard,
          numberOfDecimalPlaces = input.numberOfDecimalPlaces.transformInto[math.Positive],
          ownerId = userId
        )
      } yield analysis

      transformer.value.handleServerError
    }

  @GraphQLField
  def fetchPublicDashboardAnalysis(input: FetchDashboardAnalysisInput): Future[DashboardAnalysis] = {
    val transformer = for {
      dashboard <- EitherT.fromOptionF(
        graphQLServices.dashboardService.getById(input.dashboardId.transformInto[db.DashboardId]),
        ErrorContext.Dashboard.NotFound.asServerError
      )
      isPublic = dashboard.dashboard.visibility match {
        case services.dashboard.Visibility.Public  => true
        case services.dashboard.Visibility.Private => false
      }
      analysis <-
        if (isPublic) {
          val userId = dashboard.ownerId
          fetchAnalysis(
            dashboard = dashboard.dashboard,
            numberOfDecimalPlaces = input.numberOfDecimalPlaces.transformInto[math.Positive],
            ownerId = userId
          )
        } else EitherT.leftT[Future, DashboardAnalysis](ErrorContext.Dashboard.NotFound.asServerError)
    } yield analysis

    transformer.value.handleServerError
  }

  private def fetchAnalysis(
      dashboard: services.dashboard.Dashboard,
      numberOfDecimalPlaces: math.Positive,
      ownerId: db.UserId
  ): EitherT[Future, ServerError, DashboardAnalysis] = {
    val dashboardId = dashboard.id
    for {
      dashboard <- EitherT.fromOptionF(
        graphQLServices.dashboardService.get(ownerId, dashboardId),
        ErrorContext.Dashboard.NotFound.asServerError
      )
      entries <- EitherT.liftF[Future, ServerError, Seq[services.dashboardEntry.DashboardEntry]](
        graphQLServices.dashboardEntryService.all(ownerId, dashboardId)
      )
      projectIds = entries.map(_.projectId)
      projects <- EitherT.liftF[Future, ServerError, Seq[services.project.Project]](
        graphQLServices.projectService.allOf(ownerId, projectIds)
      )
      tasksByProjectId <- EitherT.liftF[Future, ServerError, Map[db.ProjectId, Seq[services.task.Task]]](
        graphQLServices.taskService.allFor(ownerId, projectIds)
      )
      simulations <- EitherT.liftF[Future, ServerError, Map[db.TaskId, services.simulation.Simulation]](
        graphQLServices.simulationService.all(ownerId, dashboardId)
      )
    } yield {
      val numberOfTasks         = Positive(Natural(tasksByProjectId.values.map(_.size).sum)).toOption
      val numberOfCountingTasks = Positive(Natural(tasksByProjectId.values.flatten.count(_.counting))).toOption
      val resolvedProjects = projects.map(project =>
        ProjectAnalysis(
          project = project.transformInto[Project],
          tasks = tasksByProjectId.getOrElse(project.id, Seq.empty).map { task =>
            val simulation = simulations.get(task.id)
            val incompleteStatistics = Option
              .when(!Progress.isComplete(task.progress))(
                processing.statistics.task.StatisticsService.incompleteOfTask(
                  processing.statistics.TaskWithSimulation(
                    task.transformInto[processing.statistics.Task],
                    simulation.map(_.reachedModifier)
                  ),
                  numberOfTasks,
                  numberOfCountingTasks
                )
              )
            TaskAnalysis.from(
              task = task,
              simulation = simulation,
              incompleteStatistics = incompleteStatistics,
              numberOfDecimalPlaces = numberOfDecimalPlaces
            )
          }
        )
      )
      val allTasks = resolvedProjects
        .flatMap(_.tasks)
        .map(resolvedTask =>
          TaskWithSimulation(
            task = resolvedTask.task.transformInto[processing.statistics.Task],
            simulation = resolvedTask.simulation.map(_.reachedModifier)
          )
        )

      val dashboardStatistics = StatisticsService.ofTasks(allTasks)

      DashboardAnalysis(
        dashboard.transformInto[Dashboard],
        resolvedProjects,
        (dashboardStatistics, numberOfDecimalPlaces).transformInto[DashboardStatistics]
      )
    }
  }

}
