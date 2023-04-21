package graphql.types.dashboard

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class Dashboard(
    id: DashboardId,
    header: String,
    description: Option[String],
    visibility: Visibility
)

object Dashboard {

  implicit val fromInternal: Transformer[services.dashboard.Dashboard, Dashboard] = { dashboard =>
    Dashboard(
      id = dashboard.id.transformInto[DashboardId],
      header = dashboard.header,
      description = dashboard.description,
      visibility = dashboard.visibility.transformInto[Visibility]
    )
  }

  implicit val dashboardObjectType: ObjectType[Unit, Dashboard] = deriveObjectType[Unit, Dashboard]()

}
