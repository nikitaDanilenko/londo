package services.dashboard

import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.dashboard.DashboardKey
import db.generated.Tables
import db.{ DashboardId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.common.Transactionally.syntax._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: DashboardService.Companion
)(implicit
    executionContext: ExecutionContext
) extends DashboardService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(ownerId: UserId): Future[Seq[Dashboard]] =
    db.runTransactionally(companion.all(ownerId))

  override def get(ownerId: UserId, id: DashboardId): Future[Option[Dashboard]] =
    db.runTransactionally(companion.get(ownerId, id))

  override def create(ownerId: UserId, dashboardCreation: DashboardCreation): Future[ServerError.Or[Dashboard]] =
    db.runTransactionally(companion.create(ownerId, dashboardCreation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Dashboard.Create(error.getMessage).asServerError)
      }

  override def update(
      ownerId: UserId,
      id: DashboardId,
      dashboardUpdate: DashboardUpdate
  ): Future[ServerError.Or[Dashboard]] =
    db.runTransactionally(companion.update(ownerId, id, dashboardUpdate))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Dashboard.Update(error.getMessage).asServerError)
      }

  override def delete(ownerId: UserId, id: DashboardId): Future[Boolean] =
    db.runTransactionally(companion.delete(ownerId, id))
      .recover { case _ =>
        false
      }

}

object Live {

  class Companion @Inject() (
      dao: db.daos.dashboard.DAO
  ) extends DashboardService.Companion {

    override def all(ownerId: UserId)(implicit ec: ExecutionContext): DBIO[Seq[Dashboard]] =
      dao
        .findAllFor(ownerId)
        .map(
          _.map(_.transformInto[Dashboard])
        )

    override def get(ownerId: UserId, id: DashboardId)(implicit
        ec: ExecutionContext
    ): DBIO[Option[Dashboard]] =
      OptionT(
        dao.find(DashboardKey(ownerId, id))
      )
        .map(_.transformInto[Dashboard])
        .value

    override def create(ownerId: UserId, dashboardCreation: DashboardCreation)(implicit
        ec: ExecutionContext
    ): DBIO[Dashboard] = for {
      dashboard <- DashboardCreation.create(ownerId, dashboardCreation).to[DBIO]
      dashboardRow = (dashboard, ownerId).transformInto[Tables.DashboardRow]
      inserted <- dao.insert(dashboardRow)
    } yield inserted.transformInto[Dashboard]

    override def update(ownerId: UserId, id: DashboardId, dashboardUpdate: DashboardUpdate)(implicit
        ec: ExecutionContext
    ): DBIO[Dashboard] = {
      val findAction = OptionT(get(ownerId, id)).getOrElseF(DashboardService.notFound)
      for {
        dashboard        <- findAction
        updated          <- DashboardUpdate.update(dashboard, dashboardUpdate).to[DBIO]
        _                <- dao.update(updated.transformInto[Tables.DashboardRow])
        updatedDashboard <- findAction
      } yield updatedDashboard
    }

    override def delete(ownerId: UserId, id: DashboardId)(implicit ec: ExecutionContext): DBIO[Boolean] =
      dao
        .delete(DashboardKey(ownerId, id))
        .map(_ > 0)

  }

}
