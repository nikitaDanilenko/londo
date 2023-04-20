package services.task.plain

import io.scalaland.chimney.dsl._
import math.Positive

case class PlainTaskUpdate(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    weight: Positive,
    progressUpdate: ProgressUpdate
)

object PlainTaskUpdate {

  def update(plainTask: PlainTask, plainTaskUpdate: PlainTaskUpdate): PlainTask =
    plainTask.patchUsing(plainTaskUpdate)

}
