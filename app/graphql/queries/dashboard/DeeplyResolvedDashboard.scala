package graphql.queries.dashboard

import graphql.queries.project.ResolvedProject
import graphql.types.dashboard.Dashboard
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class DeeplyResolvedDashboard(
    dashboard: Dashboard,
    resolvedProjects: Seq[ResolvedProject]
)

object DeeplyResolvedDashboard {
  implicit lazy val outputType: OutputType[DeeplyResolvedDashboard] = deriveObjectType[Unit, DeeplyResolvedDashboard]()
}
