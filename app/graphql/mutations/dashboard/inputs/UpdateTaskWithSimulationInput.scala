package graphql.mutations.dashboard.inputs

import graphql.mutations.project.inputs.TaskUpdate
import graphql.types.dashboard.DashboardId
import graphql.types.simulation.Simulation
import graphql.types.task.TaskId
import graphql.types.util.Positive
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateTaskWithSimulationInput(
    dashboardId: DashboardId,
    taskId: TaskId,
    taskUpdate: TaskUpdate,
    simulation: Option[Simulation],
    numberOfTotalTasks: Option[Positive],
    numberOfCountingTasks: Option[Positive],
    numberOfDecimalPlaces: Positive
)

object UpdateTaskWithSimulationInput {

  implicit val inputObjectType: InputObjectType[UpdateTaskWithSimulationInput] =
    deriveInputObjectType[UpdateTaskWithSimulationInput]()

}
