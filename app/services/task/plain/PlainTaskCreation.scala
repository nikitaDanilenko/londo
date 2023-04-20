package services.task.plain

import cats.effect.IO
import db.PlainTaskId
import io.scalaland.chimney.dsl._
import math.Positive
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class PlainTaskCreation(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean
)

object PlainTaskCreation {

  def create(plain: PlainTaskCreation): IO[PlainTask] = {
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[PlainTaskId])
      now <- DateUtil.now
    } yield PlainTask(
      id = id,
      name = plain.name,
      taskKind = plain.taskKind,
      unit = plain.unit,
      progress = plain.progress,
      counting = plain.counting,
      createdAt = now,
      updatedAt = None
    )

  }

}
