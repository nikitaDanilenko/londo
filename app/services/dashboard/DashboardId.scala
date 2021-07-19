package services.dashboard

import db.keys.{ DashboardReadAccessId, DashboardWriteAccessId }

import java.util.UUID

case class DashboardId(uuid: UUID) extends AnyVal {
  def asDashboardReadAccessId: DashboardReadAccessId = DashboardReadAccessId(uuid)
  def asDashboardWriteAccessId: DashboardWriteAccessId = DashboardWriteAccessId(uuid)
}
