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

class TaskKindDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[TaskKind, TaskKindDAO.Key] {
  import dbContext._
  override def findC(key: TaskKindDAO.Key): ConnectionIO[Option[TaskKind]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: TaskKind): ConnectionIO[Either[Throwable, TaskKind]] = run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[TaskKind]): ConnectionIO[Either[Throwable, List[TaskKind]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: TaskKindDAO.Key): ConnectionIO[Either[Throwable, TaskKind]] = run(deleteAction(key)).attempt
  override def replaceC(row: TaskKind): ConnectionIO[Either[Throwable, TaskKind]] = run(replaceAction(row)).attempt

  private def findAction(key: TaskKindDAO.Key) =
    quote {
      PublicSchema.TaskKindDao.query.filter(a => a.id == lift(key.uuid))
    }

  private def insertAction(row: TaskKind): Quoted[ActionReturning[TaskKind, TaskKind]] =
    quote {
      PublicSchema.TaskKindDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[TaskKind]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.TaskKindDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: TaskKindDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: TaskKind) = {
    quote {
      PublicSchema.TaskKindDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id)((t, e) => t.id -> e.id, (t, e) => t.name -> e.name)
        .returning(x => x)
    }
  }

}

object TaskKindDAO { type Key = TaskKindId }
