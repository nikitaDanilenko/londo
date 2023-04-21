package services.task

import db.TaskId

import java.time.LocalDateTime

case class Task(
    id: TaskId,
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Task {}
