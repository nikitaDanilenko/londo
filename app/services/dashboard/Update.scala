package services.dashboard

import cats.effect.IO
import utils.date.DateUtil
import io.scalaland.chimney.dsl._

case class Update(
    header: String,
    description: Option[String],
    visibility: Visibility
)

object Update {

  def update(dashboard: Dashboard, update: Update): IO[Dashboard] = {
    for {
      now <- DateUtil.now
    } yield dashboard
      .patchUsing(update)
      .copy(updatedAt = Some(now))
  }

}
