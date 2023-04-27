package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.{ DashboardId, DashboardUpdate }
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateDashboardInput(
    dashboardId: DashboardId,
    dashboardUpdate: DashboardUpdate
)

object UpdateDashboardInput {
  implicit val inputType: InputObjectType[UpdateDashboardInput] = deriveInputObjectType[UpdateDashboardInput]()
}
