package daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models.Dashboard
import doobie.implicits._
import io.getquill.ActionReturning

import java.util.UUID
import javax.inject.Inject

class DashboardDAO @Inject() (val dbContext: DbContext) {

  import dbContext.PublicSchema.DashboardDao
  import dbContext._

  def find[F[_]: Async: ContextShift](dashboardId: UUID): F[Option[Dashboard]] =
    run(findAction(dashboardId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](dashboardIds: Seq[UUID]): F[List[Dashboard]] =
    run(findAllAction(dashboardIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: Dashboard
  ): F[Dashboard] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Dashboard]): F[List[Dashboard]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](dashboardId: UUID): F[Dashboard] =
    run(deleteAction(dashboardId)).transact(transactor[F])

  def deleteAll[F[_]: Async: ContextShift](dashboardIds: Seq[UUID]): F[Long] =
    run(deleteAllAction(dashboardIds)).transact(transactor[F])

  private def findAction(dashboardId: UUID) =
    quote {
      DashboardDao.query.filter(_.id == lift(dashboardId))
    }

  private def findAllAction(dashboardIds: Seq[UUID]) = {
    val idSet = dashboardIds.toSet
    quote {
      DashboardDao.query.filter(dashboard => liftQuery(idSet).contains(dashboard.id))
    }
  }

  private def insertAction(row: Dashboard): Quoted[ActionReturning[Dashboard, Dashboard]] =
    quote {
      DashboardDao.query
        .insert(lift(row))
        .returning(x => x)
    }

  private def insertAllAction(rows: Seq[Dashboard]) =
    quote {
      liftQuery(rows).foreach(e => DashboardDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(dashboardId: UUID) =
    quote {
      findAction(dashboardId).delete
        .returning(x => x)
    }

  private def deleteAllAction(dashboardIds: Seq[UUID]) =
    quote {
      findAllAction(dashboardIds).delete
    }

}
