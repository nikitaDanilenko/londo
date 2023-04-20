package services.project

import cats.effect.IO
import io.scalaland.chimney.dsl._
import utils.date.DateUtil

case class ProjectUpdate(
    name: String,
    description: Option[String],
    flatIfSingleTask: Boolean
)

object ProjectUpdate {

  def update(project: Project, projectUpdate: ProjectUpdate): IO[Project] =
    for {
      now <- DateUtil.now
    } yield project
      .patchUsing(projectUpdate)
      .copy(updatedAt = Some(now))

}
