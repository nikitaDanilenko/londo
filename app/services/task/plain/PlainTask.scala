package services.task.plain

import db.PlainTaskId
import math.Positive

case class PlainTask(
    id: PlainTaskId,
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    weight: Positive
)

object PlainTask {

}