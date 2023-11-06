package graphql.queries.statistics

import graphql.types.simulation.Simulation
import graphql.types.task.Task
import io.scalaland.chimney.dsl._
import math.Positive
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

import java.math.MathContext

case class TaskAnalysis(
    task: Task,
    simulation: Option[Simulation],
    incompleteStatistics: Option[IncompleteTaskStatistics]
)

object TaskAnalysis {

  def from(
      task: services.task.Task,
      simulation: Option[services.simulation.Simulation],
      incompleteStatistics: Option[processing.statistics.task.IncompleteTaskStatistics],
      numberOfDecimalPlaces: Positive
  ): TaskAnalysis = TaskAnalysis(
    task = task.transformInto[Task],
    simulation = simulation.map(_.transformInto[Simulation]),
    incompleteStatistics =
      incompleteStatistics.map(s => (s, numberOfDecimalPlaces).transformInto[IncompleteTaskStatistics])
  )

  implicit lazy val outputType: OutputType[TaskAnalysis] = deriveObjectType[Unit, TaskAnalysis]()

}
