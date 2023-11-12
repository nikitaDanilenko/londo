package graphql.queries.statistics.inputs

import graphql.types.dashboard.DashboardId
import graphql.types.util.Positive
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchDashboardAnalysisInput(
    dashboardId: DashboardId,
    numberOfDecimalPlaces: Positive
)

object FetchDashboardAnalysisInput {

  implicit val inputObjectType: InputObjectType[FetchDashboardAnalysisInput] =
    deriveInputObjectType[FetchDashboardAnalysisInput]()

}
