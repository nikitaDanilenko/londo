package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardWriteAccessDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[DashboardWriteAccess]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[DashboardWriteAccess]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: DashboardWriteAccess): F[DashboardWriteAccess] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: DashboardWriteAccess): ConnectionIO[DashboardWriteAccess] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardWriteAccess]): F[List[DashboardWriteAccess]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[DashboardWriteAccess]): ConnectionIO[List[DashboardWriteAccess]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[DashboardWriteAccess] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[DashboardWriteAccess] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: DashboardWriteAccess): F[DashboardWriteAccess] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: DashboardWriteAccess): ConnectionIO[DashboardWriteAccess] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.DashboardWriteAccessDao.query.filter(a => a.dashboardId == lift(key))
    }

  private def insertAction(
      row: DashboardWriteAccess
  ): Quoted[ActionReturning[DashboardWriteAccess, DashboardWriteAccess]] =
    quote {
      PublicSchema.DashboardWriteAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardWriteAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardWriteAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardWriteAccess) = {
    quote {
      PublicSchema.DashboardWriteAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId)((t, e) => t.dashboardId -> e.dashboardId)
        .returning(x => x)
    }
  }

}
