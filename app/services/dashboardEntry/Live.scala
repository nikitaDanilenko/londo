package services.dashboardEntry

import cats.Applicative
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.dashboard.DashboardKey
import db.daos.dashboardEntry.DashboardEntryKey
import db.generated.Tables
import db.{ DashboardId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.common.Transactionally.syntax._
import services.dashboard.DashboardService
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._
import utils.transformer.implicits._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: DashboardEntryService.Companion
)(implicit
    executionContext: ExecutionContext
) extends DashboardEntryService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, dashboardId: DashboardId): Future[Seq[DashboardEntry]] =
    db.runTransactionally(companion.all(userId, dashboardId))

  override def create(
      userId: UserId,
      dashboardId: DashboardId,
      creation: Creation
  ): Future[ServerError.Or[DashboardEntry]] =
    db.runTransactionally(companion.create(userId, dashboardId, creation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.DashboardEntry.Create(error.getMessage).asServerError)
      }

  override def delete(userId: UserId, key: DashboardEntryKey): Future[ServerError.Or[Boolean]] =
    db.runTransactionally(companion.delete(userId, key))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.DashboardEntry.Delete(error.getMessage).asServerError)
      }

}

object Live {

  class Companion @Inject() (
      dashboardEntryDao: db.daos.dashboardEntry.DAO,
      dashboardDao: db.daos.dashboard.DAO
  ) extends DashboardEntryService.Companion {

    override def all(
        userId: UserId,
        dashboardId: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Seq[DashboardEntry]] =
      for {
        exists <- dashboardDao.exists(DashboardKey(userId, dashboardId))
        tasks <-
          if (exists)
            dashboardEntryDao
              .findAllFor(dashboardId)
              .map(_.map(_.transformInto[DashboardEntry]))
          else Applicative[DBIO].pure(Seq.empty)
      } yield tasks

    override def create(
        userId: UserId,
        dashboardId: DashboardId,
        creation: Creation
    )(implicit ec: ExecutionContext): DBIO[DashboardEntry] = ifDashboardExists(userId, dashboardId) {
      for {
        dashboardEntry <- Creation.create(creation).to[DBIO]
        dashboardEntryRow = (dashboardEntry, dashboardId).transformInto[Tables.DashboardEntryRow]
        inserted <- dashboardEntryDao
          .insert(dashboardEntryRow)
          .map(_.transformInto[DashboardEntry])
      } yield inserted
    }

    override def delete(
        userId: UserId,
        key: DashboardEntryKey
    )(implicit ec: ExecutionContext): DBIO[Boolean] =
      OptionT(
        dashboardEntryDao.find(key)
      ).map(_.projectId)
        .semiflatMap(dashboardId =>
          ifDashboardExists(userId, dashboardId.transformInto[DashboardId]) {
            dashboardEntryDao
              .delete(key)
              .map(_ > 0)
          }
        )
        .getOrElse(false)

    private def ifDashboardExists[A](
        userId: UserId,
        id: DashboardId
    )(action: => DBIO[A])(implicit ec: ExecutionContext): DBIO[A] =
      dashboardDao.exists(DashboardKey(userId, id)).flatMap(exists => if (exists) action else DashboardService.notFound)

  }

}
