package services.project

import cats.effect.IO
import db.{ ProjectId, UserId }
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class ProjectCreation(
    name: String,
    description: Option[String]
)

object ProjectCreation {

  def create(ownerId: UserId, projectCreation: ProjectCreation): IO[Project] = {
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[ProjectId])
      now <- DateUtil.now
    } yield Project(
      id = id,
      name = projectCreation.name,
      description = projectCreation.description,
      ownerId = ownerId,
      createdAt = now,
      updatedAt = None
    )

  }

}
