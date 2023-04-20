package services.task.plain

import db.PlainTaskId
import math.Positive

import java.util.Date

case class PlainTask(
    id: PlainTaskId,
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    weight: Positive,
    createdAt: Date,
    updatedAt: Option[Date]
)

object PlainTask {}
