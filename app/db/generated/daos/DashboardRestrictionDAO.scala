package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardRestrictionDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[DashboardRestriction]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[DashboardRestriction]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: DashboardRestriction): F[DashboardRestriction] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: DashboardRestriction): ConnectionIO[DashboardRestriction] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardRestriction]): F[List[DashboardRestriction]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[DashboardRestriction]): ConnectionIO[List[DashboardRestriction]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[DashboardRestriction] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[DashboardRestriction] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: DashboardRestriction): F[DashboardRestriction] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: DashboardRestriction): ConnectionIO[DashboardRestriction] = run(replaceAction(row))

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

  private def replaceAction(row: DashboardRestriction) = {
    quote {
      PublicSchema.DashboardRestrictionDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId)((t, e) => t.dashboardId -> e.dashboardId)
        .returning(x => x)
    }
  }

}
