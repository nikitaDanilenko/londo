package services.dashboard

import db.keys.{ DashboardReadAccessId, DashboardWriteAccessId }

import java.util.UUID

case class DashboardId(uuid: UUID) extends AnyVal {
  def asDashboardReadAccessId: DashboardReadAccessId = DashboardReadAccessId(uuid)
  def asDashboardWriteAccessId: DashboardWriteAccessId = DashboardWriteAccessId(uuid)
}

object DashboardId {

  def fromDb(dashboardId: db.keys.DashboardId): DashboardId =
    DashboardId(
      uuid = dashboardId.uuid
    )

  def toDb(dashboardId: DashboardId): db.keys.DashboardId =
    db.keys.DashboardId(
      uuid = dashboardId.uuid
    )

}
