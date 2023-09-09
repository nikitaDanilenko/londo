package graphql.queries.dashboard.inputs

import graphql.types.dashboard.DashboardId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchDeeplyResolvedDashboardInput(
    dashboardId: DashboardId
)

object FetchDeeplyResolvedDashboardInput {

  implicit val inputObjectType: InputObjectType[FetchDeeplyResolvedDashboardInput] =
    deriveInputObjectType[FetchDeeplyResolvedDashboardInput]()

}
