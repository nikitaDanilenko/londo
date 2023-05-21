package graphql.types.dashboardEntry

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec(encodeOnly = true)
case class DashboardEntry(
    projectId: ProjectId
)

object DashboardEntry {

  implicit val fromInternal: Transformer[services.dashboardEntry.DashboardEntry, DashboardEntry] = dashboard =>
    DashboardEntry(
      projectId = dashboard.projectId.transformInto[ProjectId]
    )

  implicit val objectType: ObjectType[Unit, DashboardEntry] = deriveObjectType[Unit, DashboardEntry]()
}
