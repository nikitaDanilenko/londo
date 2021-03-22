package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardRestrictionDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[DashboardRestriction]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: DashboardRestriction): F[DashboardRestriction] =
    run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardRestriction]): F[List[DashboardRestriction]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[DashboardRestriction] =
    run(deleteAction(key)).transact(transactor[F])

  private def findAction(key: UUID) =
    quote {
      PublicSchema.DashboardRestrictionDao.query.filter(a => a.dashboardId == lift(key))
    }

  private def insertAction(
      row: DashboardRestriction
  ): Quoted[ActionReturning[DashboardRestriction, DashboardRestriction]] =
    quote {
      PublicSchema.DashboardRestrictionDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardRestriction]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardRestrictionDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

}
