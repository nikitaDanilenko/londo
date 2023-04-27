package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateDashboardEntryInput(
    dashboardId: DashboardId,
    dashboardEntryCreation: DashboardEntryCreation
)

object CreateDashboardEntryInput {

  implicit val inputType: InputObjectType[CreateDashboardEntryInput] =
    deriveInputObjectType[CreateDashboardEntryInput]()

}
