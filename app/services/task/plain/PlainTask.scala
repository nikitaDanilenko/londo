package services.task.plain

import db.PlainTaskId
import math.Positive

import java.time.LocalDateTime
import java.util.Date

case class PlainTask(
    id: PlainTaskId,
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object PlainTask {}
