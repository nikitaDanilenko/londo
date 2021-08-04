package db.generated.daos

import cats.effect.{ Async, ContextShift }
import cats.syntax.applicativeError._
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardReadAccessEntryDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[DashboardReadAccessEntry, DashboardReadAccessEntryDAO.Key] {
  import dbContext._

  override def findC(key: DashboardReadAccessEntryDAO.Key): ConnectionIO[Option[DashboardReadAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: DashboardReadAccessEntry): ConnectionIO[Either[Throwable, DashboardReadAccessEntry]] =
    run(insertAction(row)).attempt

  override def insertAllC(
      rows: Seq[DashboardReadAccessEntry]
  ): ConnectionIO[Either[Throwable, List[DashboardReadAccessEntry]]] = run(insertAllAction(rows)).attempt

  override def deleteC(
      key: DashboardReadAccessEntryDAO.Key
  ): ConnectionIO[Either[Throwable, DashboardReadAccessEntry]] = run(deleteAction(key)).attempt

  override def replaceC(row: DashboardReadAccessEntry): ConnectionIO[Either[Throwable, DashboardReadAccessEntry]] =
    run(replaceAction(row)).attempt

  private def findAction(key: DashboardReadAccessEntryDAO.Key) =
    quote {
      PublicSchema.DashboardReadAccessEntryDao.query.filter(a =>
        a.dashboardReadAccessId == lift(key.dashboardReadAccessId.uuid) && a.userId == lift(key.userId.uuid)
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

  private def deleteAction(key: DashboardReadAccessEntryDAO.Key) =
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
    findByDashboardReadAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByDashboardReadAccessIdC(key: UUID): ConnectionIO[List[DashboardReadAccessEntry]] = {
    run(findByDashboardReadAccessIdAction(key))
  }

  def deleteByDashboardReadAccessId[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByDashboardReadAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByDashboardReadAccessIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByDashboardReadAccessIdAction(key)).attempt
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardReadAccessEntry]] = {
    findByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdC(key: UUID): ConnectionIO[List[DashboardReadAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByUserIdAction(key)).attempt
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

object DashboardReadAccessEntryDAO { type Key = DashboardReadAccessEntryId }
