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

class DashboardDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[Dashboard, DashboardDAO.Key] {
  import dbContext._
  override def findC(key: DashboardDAO.Key): ConnectionIO[Option[Dashboard]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: Dashboard): ConnectionIO[Dashboard] = run(insertAction(row))
  override def insertAllC(rows: Seq[Dashboard]): ConnectionIO[List[Dashboard]] = run(insertAllAction(rows))
  override def deleteC(key: DashboardDAO.Key): ConnectionIO[Dashboard] = run(deleteAction(key))
  override def replaceC(row: Dashboard): ConnectionIO[Dashboard] = run(replaceAction(row))

  private def findAction(key: DashboardDAO.Key) =
    quote {
      PublicSchema.DashboardDao.query.filter(a => a.id == lift(key.uuid))
    }

  private def insertAction(row: Dashboard): Quoted[ActionReturning[Dashboard, Dashboard]] =
    quote {
      PublicSchema.DashboardDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Dashboard]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: DashboardDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: Dashboard) = {
    quote {
      PublicSchema.DashboardDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.userId -> e.userId,
          (t, e) => t.header -> e.header,
          (t, e) => t.description -> e.description
        )
        .returning(x => x)
    }
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[Dashboard]] = {
    findByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdC(key: UUID): ConnectionIO[List[Dashboard]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}

object DashboardDAO { type Key = DashboardId }
