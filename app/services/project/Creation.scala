package services.project

import cats.effect.IO
import db.ProjectId
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class Creation(
    name: String,
    description: Option[String]
)

object Creation {

  def create(creation: Creation): IO[Project] =
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[ProjectId])
      now <- DateUtil.now
    } yield Project(
      id = id,
      name = creation.name,
      description = creation.description,
      createdAt = now,
      updatedAt = None
    )

}
