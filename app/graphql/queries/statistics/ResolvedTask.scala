package graphql.queries.statistics

import graphql.types.simulation.Simulation
import graphql.types.task.Task
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class ResolvedTask(
    task: Task,
    simulation: Option[Simulation]
)

object ResolvedTask {

  implicit lazy val outputType: OutputType[ResolvedTask] = deriveObjectType[Unit, ResolvedTask]()

}
