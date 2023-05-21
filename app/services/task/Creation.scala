package services.task

import cats.effect.IO
import db.TaskId
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class Creation(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean
)

object Creation {

  def create(creation: Creation): IO[Task] = {
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[TaskId])
      now <- DateUtil.now
    } yield Task(
      id = id,
      name = creation.name,
      taskKind = creation.taskKind,
      unit = creation.unit,
      progress = creation.progress,
      counting = creation.counting,
      createdAt = now,
      updatedAt = None
    )

  }

}
