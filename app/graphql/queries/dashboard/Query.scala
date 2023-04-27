package graphql.queries.dashboard

import graphql.types.dashboard.{Dashboard, DashboardId}
import graphql.types.task.Progress
import graphql.{HasGraphQLServices, HasLoggedInUser}
import sangria.macros.derive.GraphQLField

import scala.concurrent.Future

trait Query extends HasGraphQLServices with HasLoggedInUser {

  @GraphQLField
  def fetchDashboard(dashboardId: DashboardId): Future[Dashboard] = ???
//    validateDashboardAccess(graphQLServices.dashboardService, dashboardId, _.readAccessors.accessors)((_, dashboard) =>
//      IO.pure(ServerError.result(dashboard.fromInternal[Dashboard]))
//    )

  @GraphQLField
  def dashboardProgress(dashboardId: DashboardId): Future[Option[Progress]] = ???
//    validateDashboardAccess(graphQLServices.dashboardService, dashboardId, _.readAccessors.accessors)((_, dashboard) =>
//      IO.pure(ServerError.result(DashboardService.progress(dashboard).map(_.fromInternal[Progress])))
//    )

}
