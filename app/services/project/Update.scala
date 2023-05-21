package services.project

import cats.effect.IO
import io.scalaland.chimney.dsl._
import utils.date.DateUtil

case class Update(
    name: String,
    description: Option[String]
)

object Update {

  def update(project: Project, update: Update): IO[Project] =
    for {
      now <- DateUtil.now
    } yield project
      .patchUsing(update)
      .copy(updatedAt = Some(now))

}
