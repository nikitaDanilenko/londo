package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardCreation
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateDashboardInput(
    dashboardCreation: DashboardCreation
)

object CreateDashboardInput {
  implicit val inputType: InputObjectType[CreateDashboardInput] = deriveInputObjectType[CreateDashboardInput]()
  implicit lazy val fromInput: FromInput[CreateDashboardInput]  = circeDecoderFromInput[CreateDashboardInput]
}
