package services.task

import cats.effect.IO
import utils.date.DateUtil

case class Update(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    counting: Boolean,
    progressUpdate: ProgressUpdate
)

object Update {

  def update(task: Task, update: Update): IO[Task] =
    for {
      now <- DateUtil.now
    } yield ProgressUpdate
      .update(task, update.progressUpdate)
      .copy(
        name = update.name,
        taskKind = update.taskKind,
        unit = update.unit,
        counting = update.counting,
        updatedAt = Some(now)
      )

}
