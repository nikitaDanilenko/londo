package graphql.queries.dashboard

import graphql.types.dashboard.Dashboard
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class DeeplyResolvedDashboard(
    dashboard: Dashboard,
    resolvedProjects: Seq[DeeplyResolvedProject]
)

object DeeplyResolvedDashboard {
  implicit lazy val outputType: OutputType[DeeplyResolvedDashboard] = deriveObjectType[Unit, DeeplyResolvedDashboard]()
}
