package services.task

import db.{ ProjectId, TaskId }
import db.generated.Tables
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import math.Positive
import spire.math.Natural
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

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

  implicit val fromDB: Transformer[Tables.TaskRow, Task] = row =>
    Task(
      id = row.id.transformInto[TaskId],
      name = row.name,
      taskKind = TaskKind.withName(row.kind),
      unit = row.unit,
      progress = Progress.fraction(
        reachable = Positive.nextOf(
          Natural(row.reachable.toBigInt - 1)
        ),
        reached = Natural(row.reached.toBigInt)
      ),
      counting = row.counting,
      createdAt = row.createdAt.transformInto[LocalDateTime],
      updatedAt = row.updatedAt.map(_.transformInto[LocalDateTime])
    )

  implicit val toDB: Transformer[(Task, ProjectId), Tables.TaskRow] = { case (task, projectId) =>
    Tables.TaskRow(
      id = task.id.transformInto[UUID],
      projectId = projectId.transformInto[UUID],
      name = task.name,
      unit = task.unit,
      kind = task.taskKind.entryName,
      reached = BigDecimal(task.progress.reached),
      reachable = BigDecimal(task.progress.reachable.natural),
      counting = task.counting,
      createdAt = task.createdAt.transformInto[java.sql.Timestamp],
      updatedAt = task.updatedAt.map(_.transformInto[java.sql.Timestamp])
    )

  }

}
