package graphql.queries.dashboard

import cats.data.EitherT
import errors.{ ErrorContext, ServerError }
import graphql.HasGraphQLServices.syntax._
import graphql.queries.dashboard.inputs.{ FetchDashboardInput, FetchResolvedDashboardInput }
import graphql.queries.statistics.ResolvedDashboard
import graphql.types.dashboard.Dashboard
import graphql.types.dashboardEntry.DashboardEntry
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def fetchDashboard(input: FetchDashboardInput): Future[Dashboard] =
    withUserId { userId =>
      EitherT
        .fromOptionF(
          graphQLServices.dashboardService
            .get(
              userId,
              input.dashboardId.transformInto[db.DashboardId]
            ),
          ErrorContext.Dashboard.NotFound.asServerError
        )
        .map(_.transformInto[Dashboard])
        .value
        .handleServerError
    }

  @GraphQLField
  def fetchResolvedDashboard(input: FetchResolvedDashboardInput): Future[ResolvedDashboard] =
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
      } yield ResolvedDashboard(
        dashboard.transformInto[Dashboard],
        entries.map(_.transformInto[DashboardEntry])
      )

      transformer.value.handleServerError
    }

  @GraphQLField
  def fetchAllDashboards: Future[Seq[Dashboard]] =
    withUserId { userId =>
      graphQLServices.dashboardService
        .all(userId)
        .map(_.map(_.transformInto[Dashboard]))
    }

}
