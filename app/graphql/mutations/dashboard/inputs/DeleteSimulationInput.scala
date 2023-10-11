package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteSimulationInput(
    dashboardId: DashboardId,
    taskId: TaskId
)

object DeleteSimulationInput {

  implicit val inputObjectType: InputObjectType[DeleteSimulationInput] =
    deriveInputObjectType[DeleteSimulationInput]()

}
