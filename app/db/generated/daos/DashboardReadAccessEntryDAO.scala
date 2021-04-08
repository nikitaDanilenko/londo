package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardReadAccessEntryDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[DashboardReadAccessEntry]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: (UUID, UUID)): ConnectionIO[Option[DashboardReadAccessEntry]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: DashboardReadAccessEntry): F[DashboardReadAccessEntry] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: DashboardReadAccessEntry): ConnectionIO[DashboardReadAccessEntry] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[DashboardReadAccessEntry]): F[List[DashboardReadAccessEntry]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[DashboardReadAccessEntry]): ConnectionIO[List[DashboardReadAccessEntry]] =
    run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[DashboardReadAccessEntry] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: (UUID, UUID)): ConnectionIO[DashboardReadAccessEntry] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: DashboardReadAccessEntry): F[DashboardReadAccessEntry] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: DashboardReadAccessEntry): ConnectionIO[DashboardReadAccessEntry] = run(replaceAction(row))

  private def findAction(key: (UUID, UUID)) =
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query.filter(a =>
        a.dashboardReadAccessId == lift(key._1) && a.userId == lift(key._2)
      )
    }

  private def insertAction(
      row: DashboardReadAccessEntry
  ): Quoted[ActionReturning[DashboardReadAccessEntry, DashboardReadAccessEntry]] =
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardReadAccessEntry]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardReadAccessEntryDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: (UUID, UUID)) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardReadAccessEntry) = {
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardReadAccessId, _.userId)(
          (t, e) => t.dashboardReadAccessId -> e.dashboardReadAccessId,
          (t, e) => t.userId -> e.userId
        )
        .returning(x => x)
    }
  }

  def findByDashboardReadAccessId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardReadAccessEntry]] = {
    findByDashboardReadAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByDashboardReadAccessIdF(key: UUID): ConnectionIO[List[DashboardReadAccessEntry]] = {
    run(findByDashboardReadAccessIdAction(key))
  }

  def deleteByDashboardReadAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByDashboardReadAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByDashboardReadAccessIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByDashboardReadAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardReadAccessEntry]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[DashboardReadAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByDashboardReadAccessIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query.filter(a => a.dashboardReadAccessId == lift(key))
    }
  }

  private def deleteByDashboardReadAccessIdAction(key: UUID) = {
    quote {
      findByDashboardReadAccessIdAction(key).delete
    }
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
