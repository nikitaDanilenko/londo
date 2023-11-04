package graphql.queries.statistics

import graphql.types.simulation.Simulation
import graphql.types.task.Task
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class TaskAnalysis(
    task: Task,
    simulation: Option[Simulation]
)

object TaskAnalysis {

  implicit lazy val outputType: OutputType[TaskAnalysis] = deriveObjectType[Unit, TaskAnalysis]()

}
