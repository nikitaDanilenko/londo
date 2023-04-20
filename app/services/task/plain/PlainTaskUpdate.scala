package services.task.plain

import cats.effect.IO
import io.scalaland.chimney.dsl._
import math.Positive
import utils.date.DateUtil

case class PlainTaskUpdate(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    weight: Positive,
    progressUpdate: ProgressUpdate
)

object PlainTaskUpdate {

  def update(plainTask: PlainTask, plainTaskUpdate: PlainTaskUpdate): IO[PlainTask] =
    for {
      now <- DateUtil.now
    } yield plainTask
      .patchUsing(plainTaskUpdate)
      .copy(updatedAt = Some(now))

}
