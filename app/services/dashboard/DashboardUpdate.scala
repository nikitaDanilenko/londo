package services.dashboard

import cats.effect.IO
import utils.date.DateUtil
import io.scalaland.chimney.dsl._

case class DashboardUpdate(
    header: String,
    description: Option[String],
    publiclyVisible: Boolean
)

object DashboardUpdate {

  def update(dashboard: Dashboard, dashboardUpdate: DashboardUpdate): IO[Dashboard] = {
    for {
      now <- DateUtil.now
    } yield dashboard
      .patchUsing(dashboardUpdate)
      .copy(updatedAt = Some(now))
  }

}
