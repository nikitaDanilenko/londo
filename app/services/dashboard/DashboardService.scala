package services.dashboard

import db.{ DashboardId, UserId }
import errors.ServerError
import services.DBError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait DashboardService {
  def all(ownerId: UserId): Future[Seq[Dashboard]]

  def get(ownerId: UserId, id: DashboardId): Future[Option[Dashboard]]

  def create(
      ownerId: UserId,
      dashboardCreation: DashboardCreation
  ): Future[ServerError.Or[Dashboard]]

  def update(
      ownerId: UserId,
      id: DashboardId,
      dashboardUpdate: DashboardUpdate
  ): Future[ServerError.Or[Dashboard]]

  def delete(
      ownerId: UserId,
      id: DashboardId
  ): Future[Boolean]

}

object DashboardService {

  trait Companion {
    def all(ownerId: UserId)(implicit ec: ExecutionContext): DBIO[Seq[Dashboard]]

    def get(
        ownerId: UserId,
        id: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Option[Dashboard]]

    def create(
        ownerId: UserId,
        dashboardCreation: DashboardCreation
    )(implicit ec: ExecutionContext): DBIO[Dashboard]

    def update(
        ownerId: UserId,
        id: DashboardId,
        dashboardUpdate: DashboardUpdate
    )(implicit ec: ExecutionContext): DBIO[Dashboard]

    def delete(
        ownerId: UserId,
        id: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

  def notFound[A]: DBIO[A] = DBIO.failed(DBError.Dashboard.NotFound)

}
