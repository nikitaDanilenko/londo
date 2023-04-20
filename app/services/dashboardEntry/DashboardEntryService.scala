package services.dashboardEntry

import db.daos.dashboardEntry.DashboardEntryKey
import db.{ DashboardId, UserId }
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait DashboardEntryService {

  def all(userId: UserId, dashboardId: DashboardId): Future[Seq[DashboardEntry]]

  def create(
      userId: UserId,
      dashboardId: DashboardId,
      dashboardEntryCreation: DashboardEntryCreation
  ): Future[ServerError.Or[DashboardEntry]]

  def delete(
      userId: UserId,
      key: DashboardEntryKey
  ): Future[ServerError.Or[Boolean]]

}

object DashboardEntryService {

  trait Companion {

    def all(
        userId: UserId,
        dashboardId: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Seq[DashboardEntry]]

    def create(
        userId: UserId,
        dashboardId: DashboardId,
        dashboardEntryCreation: DashboardEntryCreation
    )(implicit ec: ExecutionContext): DBIO[DashboardEntry]

    def delete(
        userId: UserId,
        key: DashboardEntryKey
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

}
