package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteDashboardInput(
    dashboardId: DashboardId
)

object DeleteDashboardInput {
  implicit val inputObjectType: InputObjectType[DeleteDashboardInput] = deriveInputObjectType[DeleteDashboardInput]()
}
