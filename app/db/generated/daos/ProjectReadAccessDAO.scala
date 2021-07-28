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

class ProjectReadAccessDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[ProjectReadAccess, ProjectReadAccessDAO.Key] {
  import dbContext._

  override def findC(key: ProjectReadAccessDAO.Key): ConnectionIO[Option[ProjectReadAccess]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: ProjectReadAccess): ConnectionIO[Either[Throwable, ProjectReadAccess]] =
    run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[ProjectReadAccess]): ConnectionIO[Either[Throwable, List[ProjectReadAccess]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: ProjectReadAccessDAO.Key): ConnectionIO[Either[Throwable, ProjectReadAccess]] =
    run(deleteAction(key)).attempt

  override def replaceC(row: ProjectReadAccess): ConnectionIO[Either[Throwable, ProjectReadAccess]] =
    run(replaceAction(row)).attempt

  private def findAction(key: ProjectReadAccessDAO.Key) =
    quote {
      PublicSchema.ProjectReadAccessDao.query.filter(a => a.projectId == lift(key.uuid))
    }

  private def insertAction(row: ProjectReadAccess): Quoted[ActionReturning[ProjectReadAccess, ProjectReadAccess]] =
    quote {
      PublicSchema.ProjectReadAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectReadAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectReadAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: ProjectReadAccessDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectReadAccess) = {
    quote {
      PublicSchema.ProjectReadAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId)((t, e) => t.projectId -> e.projectId, (t, e) => t.isAllowList -> e.isAllowList)
        .returning(x => x)
    }
  }

}

object ProjectReadAccessDAO { type Key = ProjectReadAccessId }
