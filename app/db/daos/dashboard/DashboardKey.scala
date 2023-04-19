package db.daos.dashboard

import db.generated.Tables
import db.{ DashboardId, UserId }
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

case class DashboardKey(
    userId: UserId,
    dashboardId: DashboardId
)

object DashboardKey {

  def of(row: Tables.DashboardRow): DashboardKey =
    DashboardKey(
      row.userId.transformInto[UserId],
      row.id.transformInto[DashboardId]
    )

}
