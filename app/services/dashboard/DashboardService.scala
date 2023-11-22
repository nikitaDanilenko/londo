package services.dashboard

import db.{ DashboardId, UserId }
import errors.ServerError
import services.DBError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait DashboardService {
  def all(ownerId: UserId): Future[Seq[Dashboard]]

  def get(ownerId: UserId, id: DashboardId): Future[Option[Dashboard]]

  def getById(id: DashboardId): Future[Option[DashboardWithOwner]]

  def create(
      ownerId: UserId,
      creation: Creation
  ): Future[ServerError.Or[Dashboard]]

  def update(
      ownerId: UserId,
      id: DashboardId,
      update: Update
  ): Future[ServerError.Or[Dashboard]]

  def delete(
      ownerId: UserId,
      id: DashboardId
  ): Future[ServerError.Or[Boolean]]

}

object DashboardService {

  trait Companion {
    def all(ownerId: UserId)(implicit ec: ExecutionContext): DBIO[Seq[Dashboard]]

    def get(
        ownerId: UserId,
        id: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Option[Dashboard]]

    def getById(
        id: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Option[DashboardWithOwner]]

    def create(
        ownerId: UserId,
        creation: Creation
    )(implicit ec: ExecutionContext): DBIO[Dashboard]

    def update(
        ownerId: UserId,
        id: DashboardId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[Dashboard]

    def delete(
        ownerId: UserId,
        id: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

  def notFound[A]: DBIO[A] = DBIO.failed(DBError.Dashboard.NotFound)

}
