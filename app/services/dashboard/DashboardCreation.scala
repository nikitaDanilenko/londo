package services.dashboard

import cats.effect.IO
import db.{ DashboardId, UserId }
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class DashboardCreation(
    header: String,
    description: Option[String],
    publiclyVisible: Boolean
)

object DashboardCreation {

  def create(ownerId: UserId, dashboardCreation: DashboardCreation): IO[Dashboard] = {
    for {
      id  <- RandomGenerator.randomUUID.map(_.transformInto[DashboardId])
      now <- DateUtil.now
    } yield Dashboard(
      id = id,
      header = dashboardCreation.header,
      description = dashboardCreation.description,
      ownerId = ownerId,
      publiclyVisible = dashboardCreation.publiclyVisible,
      createdAt = now,
      updatedAt = None
    )
  }

}
