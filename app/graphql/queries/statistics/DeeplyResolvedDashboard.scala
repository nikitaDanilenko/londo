package graphql.queries.statistics

import graphql.types.dashboard.Dashboard
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class DeeplyResolvedDashboard(
    dashboard: Dashboard,
    resolvedProjects: Seq[DeeplyResolvedProject],
    dashboardStatistics: DashboardStatistics
)

object DeeplyResolvedDashboard {
  implicit lazy val outputType: OutputType[DeeplyResolvedDashboard] = deriveObjectType[Unit, DeeplyResolvedDashboard]()
}
