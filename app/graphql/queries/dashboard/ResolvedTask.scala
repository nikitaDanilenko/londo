package graphql.queries.dashboard

import graphql.types.simulation.Simulation
import graphql.types.task.Task
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

@JsonCodec
case class ResolvedTask(
    task: Task,
    simulation: Option[Simulation]
)

object ResolvedTask {

  implicit lazy val outputType: OutputType[ResolvedTask] = deriveObjectType[Unit, ResolvedTask]()

}
