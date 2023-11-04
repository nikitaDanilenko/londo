package graphql.queries.statistics

import graphql.types.dashboard.Dashboard
import graphql.types.dashboardEntry.DashboardEntry
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class ResolvedDashboard(
    dashboard: Dashboard,
    entries: Seq[DashboardEntry]
)

object ResolvedDashboard {
  implicit lazy val outputType: OutputType[ResolvedDashboard] = deriveObjectType[Unit, ResolvedDashboard]()
}
