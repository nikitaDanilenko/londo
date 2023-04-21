package services.task

import db.{ ProjectId, TaskId }
import db.generated.Tables
import io.scalaland.chimney.Transformer

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

object Task {
  implicit val fromDB: Transformer[Tables.TaskRow, Task]            = ???
  implicit val toDB: Transformer[(Task, ProjectId), Tables.TaskRow] = ???
}
