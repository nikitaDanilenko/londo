package graphql.queries.statistics

import graphql.types.dashboard.Dashboard
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class DashboardAnalysis(
    dashboard: Dashboard,
    projectAnalyses: Seq[ProjectAnalysis],
    dashboardStatistics: DashboardStatistics
)

object DashboardAnalysis {
  implicit lazy val outputType: OutputType[DashboardAnalysis] = deriveObjectType[Unit, DashboardAnalysis]()
}
