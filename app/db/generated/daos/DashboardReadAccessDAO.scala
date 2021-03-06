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

class DashboardReadAccessDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[DashboardReadAccess, DashboardReadAccessDAO.Key] {
  import dbContext._

  override def findC(key: DashboardReadAccessDAO.Key): ConnectionIO[Option[DashboardReadAccess]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: DashboardReadAccess): ConnectionIO[DashboardReadAccess] = run(insertAction(row))

  override def insertAllC(rows: Seq[DashboardReadAccess]): ConnectionIO[List[DashboardReadAccess]] =
    run(insertAllAction(rows))

  override def deleteC(key: DashboardReadAccessDAO.Key): ConnectionIO[DashboardReadAccess] = run(deleteAction(key))
  override def replaceC(row: DashboardReadAccess): ConnectionIO[DashboardReadAccess] = run(replaceAction(row))

  private def findAction(key: DashboardReadAccessDAO.Key) =
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

  private def deleteAction(key: DashboardReadAccessDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardReadAccess) = {
    quote {
      PublicSchema.DashboardReadAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId)(
          (t, e) => t.dashboardId -> e.dashboardId,
          (t, e) => t.isAllowList -> e.isAllowList
        )
        .returning(x => x)
    }
  }

}

object DashboardReadAccessDAO { type Key = DashboardReadAccessId }
