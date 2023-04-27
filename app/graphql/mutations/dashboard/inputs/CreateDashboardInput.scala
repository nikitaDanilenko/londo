package graphql.mutations.dashboard.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateDashboardInput(
    dashboardCreation: DashboardCreation
)

object CreateDashboardInput {
  implicit val inputType: InputObjectType[CreateDashboardInput] = deriveInputObjectType[CreateDashboardInput]()
}
