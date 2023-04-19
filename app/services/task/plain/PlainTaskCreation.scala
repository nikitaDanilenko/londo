package services.task.plain

import cats.effect.IO
import db.PlainTaskId
import io.scalaland.chimney.dsl._
import math.Positive
import services.task.TaskKind
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class PlainTaskCreation(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    weight: Positive
)

object PlainTaskCreation {

  def create(plain: PlainTaskCreation): IO[PlainTask] =
    RandomGenerator.randomUUID.map { uuid =>
      PlainTask(
        id = uuid.transformInto[PlainTaskId],
        name = plain.name,
        taskKind = plain.taskKind,
        unit = plain.unit,
        progress = plain.progress,
        weight = plain.weight
      )
    }

}
