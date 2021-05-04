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

class ProjectWriteAccessDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[ProjectWriteAccess, ProjectWriteAccessDAO.Key] {
  import dbContext._

  override def findC(key: ProjectWriteAccessDAO.Key): ConnectionIO[Option[ProjectWriteAccess]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: ProjectWriteAccess): ConnectionIO[ProjectWriteAccess] = run(insertAction(row))

  override def insertAllC(rows: Seq[ProjectWriteAccess]): ConnectionIO[List[ProjectWriteAccess]] =
    run(insertAllAction(rows))

  override def deleteC(key: ProjectWriteAccessDAO.Key): ConnectionIO[ProjectWriteAccess] = run(deleteAction(key))
  override def replaceC(row: ProjectWriteAccess): ConnectionIO[ProjectWriteAccess] = run(replaceAction(row))

  private def findAction(key: ProjectWriteAccessDAO.Key) =
    quote {
      PublicSchema.ProjectWriteAccessDao.query.filter(a => a.projectId == lift(key.uuid))
    }

  private def insertAction(row: ProjectWriteAccess): Quoted[ActionReturning[ProjectWriteAccess, ProjectWriteAccess]] =
    quote {
      PublicSchema.ProjectWriteAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectWriteAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectWriteAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: ProjectWriteAccessDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectWriteAccess) = {
    quote {
      PublicSchema.ProjectWriteAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId)((t, e) => t.projectId -> e.projectId, (t, e) => t.isAllowList -> e.isAllowList)
        .returning(x => x)
    }
  }

}

object ProjectWriteAccessDAO { type Key = ProjectWriteAccessId }
