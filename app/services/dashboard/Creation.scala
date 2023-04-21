package services.dashboard

import cats.effect.IO
import db.{ DashboardId, UserId }
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class Creation(
    header: String,
    description: Option[String],
    visibility: Visibility
)

object Creation {

  def create(ownerId: UserId, creation: Creation): IO[Dashboard] = {
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[DashboardId])
      now <- DateUtil.now
    } yield Dashboard(
      id = id,
      header = creation.header,
      description = creation.description,
      ownerId = ownerId,
      visibility = creation.visibility,
      createdAt = now,
      updatedAt = None
    )
  }

}
