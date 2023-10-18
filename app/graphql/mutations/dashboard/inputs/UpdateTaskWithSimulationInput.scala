package graphql.mutations.dashboard.inputs

import graphql.mutations.project.inputs.TaskUpdate
import graphql.types.dashboard.DashboardId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateTaskWithSimulationInput(
    dashboardId: DashboardId,
    taskId: TaskId,
    taskUpdate: TaskUpdate,
    simulationUpdate: SimulationUpdate
)

object UpdateTaskWithSimulationInput {

  implicit val inputObjectType: InputObjectType[UpdateTaskWithSimulationInput] =
    deriveInputObjectType[UpdateTaskWithSimulationInput]()

}
