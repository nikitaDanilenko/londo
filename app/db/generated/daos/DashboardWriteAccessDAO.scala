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

class DashboardWriteAccessDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[DashboardWriteAccess, DashboardWriteAccessDAO.Key] {
  import dbContext._

  override def findC(key: DashboardWriteAccessDAO.Key): ConnectionIO[Option[DashboardWriteAccess]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: DashboardWriteAccess): ConnectionIO[DashboardWriteAccess] = run(insertAction(row))

  override def insertAllC(rows: Seq[DashboardWriteAccess]): ConnectionIO[List[DashboardWriteAccess]] =
    run(insertAllAction(rows))

  override def deleteC(key: DashboardWriteAccessDAO.Key): ConnectionIO[DashboardWriteAccess] = run(deleteAction(key))
  override def replaceC(row: DashboardWriteAccess): ConnectionIO[DashboardWriteAccess] = run(replaceAction(row))

  private def findAction(key: DashboardWriteAccessDAO.Key) =
    quote {
      PublicSchema.DashboardWriteAccessDao.query.filter(a => a.dashboardId == lift(key.uuid))
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

  private def deleteAction(key: DashboardWriteAccessDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardWriteAccess) = {
    quote {
      PublicSchema.DashboardWriteAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId)(
          (t, e) => t.dashboardId -> e.dashboardId,
          (t, e) => t.isAllowList -> e.isAllowList
        )
        .returning(x => x)
    }
  }

}

object DashboardWriteAccessDAO { type Key = DashboardWriteAccessId }
