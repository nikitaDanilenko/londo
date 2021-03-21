package daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models.DashboardRestriction
import doobie.implicits._
import io.getquill.ActionReturning

import java.util.UUID
import javax.inject.Inject

class DashboardRestrictionDAO @Inject() (val dbContext: DbContext) {

  import dbContext.PublicSchema.DashboardRestrictionDao
  import dbContext._

  def find[F[_]: Async: ContextShift](dashboardRestrictionId: UUID): F[Option[DashboardRestriction]] =
    run(findAction(dashboardRestrictionId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](dashboardRestrictionIds: Seq[UUID]): F[List[DashboardRestriction]] =
    run(findAllAction(dashboardRestrictionIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: DashboardRestriction
  ): F[DashboardRestriction] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardRestriction]): F[List[DashboardRestriction]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](dashboardRestrictionId: UUID): F[DashboardRestriction] =
    run(deleteAction(dashboardRestrictionId)).transact(transactor[F])

  def deleteAll[F[_]: Async: ContextShift](dashboardRestrictionIds: Seq[UUID]): F[Long] =
    run(deleteAllAction(dashboardRestrictionIds)).transact(transactor[F])

  private def findAction(dashboardRestrictionId: UUID) =
    quote {
      DashboardRestrictionDao.query.filter(_.dashboardId == lift(dashboardRestrictionId))
    }

  private def findAllAction(dashboardRestrictionIds: Seq[UUID]) = {
    val idSet = dashboardRestrictionIds.toSet
    quote {
      DashboardRestrictionDao.query.filter(dashboardRestriction =>
        liftQuery(idSet).contains(dashboardRestriction.dashboardId)
      )
    }
  }

  private def insertAction(
      row: DashboardRestriction
  ): Quoted[ActionReturning[DashboardRestriction, DashboardRestriction]] =
    quote {
      DashboardRestrictionDao.query
        .insert(lift(row))
        .returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardRestriction]) =
    quote {
      liftQuery(rows).foreach(e => DashboardRestrictionDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(dashboardRestrictionId: UUID) =
    quote {
      findAction(dashboardRestrictionId).delete
        .returning(x => x)
    }

  private def deleteAllAction(dashboardRestrictionIds: Seq[UUID]) =
    quote {
      findAllAction(dashboardRestrictionIds).delete
    }

}
