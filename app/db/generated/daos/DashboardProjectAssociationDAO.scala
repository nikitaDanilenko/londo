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

class DashboardProjectAssociationDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[DashboardProjectAssociation, DashboardProjectAssociationDAO.Key] {
  import dbContext._

  override def findC(key: DashboardProjectAssociationDAO.Key): ConnectionIO[Option[DashboardProjectAssociation]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: DashboardProjectAssociation): ConnectionIO[DashboardProjectAssociation] =
    run(insertAction(row))

  override def insertAllC(rows: Seq[DashboardProjectAssociation]): ConnectionIO[List[DashboardProjectAssociation]] =
    run(insertAllAction(rows))

  override def deleteC(key: DashboardProjectAssociationDAO.Key): ConnectionIO[DashboardProjectAssociation] =
    run(deleteAction(key))

  override def replaceC(row: DashboardProjectAssociation): ConnectionIO[DashboardProjectAssociation] =
    run(replaceAction(row))

  private def findAction(key: DashboardProjectAssociationDAO.Key) =
    quote {
      PublicSchema.DashboardProjectAssociationDao.query.filter(a =>
        a.dashboardId == lift(key.dashboardId.uuid) && a.projectId == lift(key.projectId.uuid)
      )
    }

  private def insertAction(
      row: DashboardProjectAssociation
  ): Quoted[ActionReturning[DashboardProjectAssociation, DashboardProjectAssociation]] =
    quote {
      PublicSchema.DashboardProjectAssociationDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[DashboardProjectAssociation]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardProjectAssociationDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: DashboardProjectAssociationDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: DashboardProjectAssociation) = {
    quote {
      PublicSchema.DashboardProjectAssociationDao.query
        .insert(lift(row))
        .onConflictUpdate(_.dashboardId, _.projectId)(
          (t, e) => t.dashboardId -> e.dashboardId,
          (t, e) => t.projectId -> e.projectId,
          (t, e) => t.weight -> e.weight
        )
        .returning(x => x)
    }
  }

  def findByDashboardId[F[_]: Async: ContextShift](key: UUID): F[List[DashboardProjectAssociation]] = {
    findByDashboardIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByDashboardIdC(key: UUID): ConnectionIO[List[DashboardProjectAssociation]] = {
    run(findByDashboardIdAction(key))
  }

  def deleteByDashboardId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByDashboardIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByDashboardIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByDashboardIdAction(key))
  }

  private def findByDashboardIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardProjectAssociationDao.query.filter(a => a.dashboardId == lift(key))
    }
  }

  private def deleteByDashboardIdAction(key: UUID) = {
    quote {
      findByDashboardIdAction(key).delete
    }
  }

}

object DashboardProjectAssociationDAO { type Key = DashboardProjectAssociationId }
