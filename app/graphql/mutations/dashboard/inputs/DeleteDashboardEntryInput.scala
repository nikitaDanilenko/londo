package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteDashboardEntryInput(
    dashboardId: DashboardId,
    projectId: ProjectId
)

object DeleteDashboardEntryInput {

  implicit val inputObjectType: InputObjectType[DeleteDashboardEntryInput] =
    deriveInputObjectType[DeleteDashboardEntryInput]()

}
