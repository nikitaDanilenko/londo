package graphql.queries.dashboard.inputs

import graphql.types.dashboard.DashboardId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchResolvedDashboardInput(
    dashboardId: DashboardId
)

object FetchResolvedDashboardInput {

  implicit val inputObjectType: InputObjectType[FetchResolvedDashboardInput] =
    deriveInputObjectType[FetchResolvedDashboardInput]()

}
