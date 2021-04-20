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

class DashboardWriteAccessEntryDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: DashboardWriteAccessEntryId): F[Option[DashboardWriteAccessEntry]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: DashboardWriteAccessEntryId): ConnectionIO[Option[DashboardWriteAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: DashboardWriteAccessEntry): F[DashboardWriteAccessEntry] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: DashboardWriteAccessEntry): ConnectionIO[DashboardWriteAccessEntry] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardWriteAccessEntry]): F[List[DashboardWriteAccessEntry]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[DashboardWriteAccessEntry]): ConnectionIO[List[DashboardWriteAccessEntry]] =
    run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: DashboardWriteAccessEntryId): F[DashboardWriteAccessEntry] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: DashboardWriteAccessEntryId): ConnectionIO[DashboardWriteAccessEntry] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: DashboardWriteAccessEntry): F[DashboardWriteAccessEntry] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: DashboardWriteAccessEntry): ConnectionIO[DashboardWriteAccessEntry] = run(replaceAction(row))

  private def findAction(key: DashboardWriteAccessEntryId) =
    quote {
      PublicSchema.DashboardWriteAccessEntryDao.query.filter(a =>
        a.dashboardWriteAccessId == lift(key.dashboardWriteAccessId.uuid) && a.userId == lift(key.userId.uuid)
      )
    }

  private def insertAction(
      row: DashboardWriteAccessEntry
  ): Quoted[ActionReturning[DashboardWriteAccessEntry, DashboardWriteAccessEntry]] =
    quote {
      PublicSchema.DashboardWriteAccessEntryDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardWriteAccessEntry]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardWriteAccessEntryDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: DashboardWriteAccessEntryId) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardWriteAccessEntry) = {
    quote {
      PublicSchema.DashboardWriteAccessEntryDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardWriteAccessId, _.userId)(
          (t, e) => t.dashboardWriteAccessId -> e.dashboardWriteAccessId,
          (t, e) => t.userId -> e.userId
        )
        .returning(x => x)
    }
  }

  def findByDashboardWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardWriteAccessEntry]] = {
    findByDashboardWriteAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByDashboardWriteAccessIdF(key: UUID): ConnectionIO[List[DashboardWriteAccessEntry]] = {
    run(findByDashboardWriteAccessIdAction(key))
  }

  def deleteByDashboardWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByDashboardWriteAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByDashboardWriteAccessIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByDashboardWriteAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardWriteAccessEntry]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[DashboardWriteAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByDashboardWriteAccessIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardWriteAccessEntryDao.query.filter(a => a.dashboardWriteAccessId == lift(key))
    }
  }

  private def deleteByDashboardWriteAccessIdAction(key: UUID) = {
    quote {
      findByDashboardWriteAccessIdAction(key).delete
    }
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardWriteAccessEntryDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
