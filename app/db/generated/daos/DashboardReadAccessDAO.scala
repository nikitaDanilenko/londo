package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardReadAccessDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: DashboardReadAccessId): F[Option[DashboardReadAccess]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: DashboardReadAccessId): ConnectionIO[Option[DashboardReadAccess]] =
    run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: DashboardReadAccess): F[DashboardReadAccess] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: DashboardReadAccess): ConnectionIO[DashboardReadAccess] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardReadAccess]): F[List[DashboardReadAccess]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[DashboardReadAccess]): ConnectionIO[List[DashboardReadAccess]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: DashboardReadAccessId): F[DashboardReadAccess] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: DashboardReadAccessId): ConnectionIO[DashboardReadAccess] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: DashboardReadAccess): F[DashboardReadAccess] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: DashboardReadAccess): ConnectionIO[DashboardReadAccess] = run(replaceAction(row))

  private def findAction(key: DashboardReadAccessId) =
    quote {
      PublicSchema.DashboardReadAccessDao.query.filter(a => a.dashboardId == lift(key.uuid))
    }

  private def insertAction(
      row: DashboardReadAccess
  ): Quoted[ActionReturning[DashboardReadAccess, DashboardReadAccess]] =
    quote {
      PublicSchema.DashboardReadAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardReadAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardReadAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: DashboardReadAccessId) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardReadAccess) = {
    quote {
      PublicSchema.DashboardReadAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId)((t, e) => t.dashboardId -> e.dashboardId)
        .returning(x => x)
    }
  }

}
