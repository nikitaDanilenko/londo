package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateDashboardInput(
    dashboardId: DashboardId,
    dashboardUpdate: DashboardUpdate
)

object UpdateDashboardInput {
  implicit val inputObjectType: InputObjectType[UpdateDashboardInput] = deriveInputObjectType[UpdateDashboardInput]()
}
