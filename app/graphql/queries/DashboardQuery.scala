package graphql.queries

import cats.effect.IO
import errors.ServerError
import graphql.types.FromInternal.syntax._
import graphql.types.dashboard.{ Dashboard, DashboardId }
import graphql.types.task.Progress
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import services.dashboard.DashboardService

import scala.concurrent.Future

trait DashboardQuery extends HasGraphQLServices with HasLoggedInUser {

  import ioImplicits._

  @GraphQLField
  def fetchDashboard(dashboardId: DashboardId): Future[Dashboard] =
    validateDashboardAccess(graphQLServices.dashboardService, dashboardId, _.readAccessors.accessors)((_, dashboard) =>
      IO.pure(ServerError.result(dashboard.fromInternal[Dashboard]))
    )

  @GraphQLField
  def dashboardProgress(dashboardId: DashboardId): Future[Option[Progress]] =
    validateDashboardAccess(graphQLServices.dashboardService, dashboardId, _.readAccessors.accessors)((_, dashboard) =>
      IO.pure(ServerError.result(DashboardService.progress(dashboard).map(_.fromInternal[Progress])))
    )

}
