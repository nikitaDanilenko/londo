package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.DashboardId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateSimulationInput(
    dashboardId: DashboardId,
    taskId: TaskId,
    simulationCreation: SimulationCreation
)

object CreateSimulationInput {

  implicit val inputObjectType: InputObjectType[CreateSimulationInput] =
    deriveInputObjectType[CreateSimulationInput]()

}
