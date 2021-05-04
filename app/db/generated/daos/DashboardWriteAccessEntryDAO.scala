package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardWriteAccessEntryDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[DashboardWriteAccessEntry, DashboardWriteAccessEntryDAO.Key] {
  import dbContext._

  override def findC(key: DashboardWriteAccessEntryDAO.Key): ConnectionIO[Option[DashboardWriteAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: DashboardWriteAccessEntry): ConnectionIO[DashboardWriteAccessEntry] = run(insertAction(row))

  override def insertAllC(rows: Seq[DashboardWriteAccessEntry]): ConnectionIO[List[DashboardWriteAccessEntry]] =
    run(insertAllAction(rows))

  override def deleteC(key: DashboardWriteAccessEntryDAO.Key): ConnectionIO[DashboardWriteAccessEntry] =
    run(deleteAction(key))

  override def replaceC(row: DashboardWriteAccessEntry): ConnectionIO[DashboardWriteAccessEntry] =
    run(replaceAction(row))

  private def findAction(key: DashboardWriteAccessEntryDAO.Key) =
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

  private def deleteAction(key: DashboardWriteAccessEntryDAO.Key) =
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
    findByDashboardWriteAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByDashboardWriteAccessIdC(key: UUID): ConnectionIO[List[DashboardWriteAccessEntry]] = {
    run(findByDashboardWriteAccessIdAction(key))
  }

  def deleteByDashboardWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByDashboardWriteAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByDashboardWriteAccessIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByDashboardWriteAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardWriteAccessEntry]] = {
    findByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdC(key: UUID): ConnectionIO[List[DashboardWriteAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdC(key: UUID): ConnectionIO[Long] = {
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

object DashboardWriteAccessEntryDAO { type Key = DashboardWriteAccessEntryId }
