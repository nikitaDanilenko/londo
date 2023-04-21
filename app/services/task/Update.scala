package services.task

import cats.effect.IO
import io.scalaland.chimney.dsl._
import utils.date.DateUtil

case class Update(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    counting: Boolean,
    progressUpdate: ProgressUpdate
)

object Update {

  def update(plainTask: Task, plainTaskUpdate: Update): IO[Task] =
    for {
      now <- DateUtil.now
    } yield plainTask
      .patchUsing(plainTaskUpdate)
      .copy(updatedAt = Some(now))

}
