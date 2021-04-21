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

class TaskDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[Task, TaskDAO.Key] {
  import dbContext._
  override def findC(key: TaskDAO.Key): ConnectionIO[Option[Task]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: Task): ConnectionIO[Task] = run(insertAction(row))
  override def insertAllC(rows: Seq[Task]): ConnectionIO[List[Task]] = run(insertAllAction(rows))
  override def deleteC(key: TaskDAO.Key): ConnectionIO[Task] = run(deleteAction(key))
  override def replaceC(row: Task): ConnectionIO[Task] = run(replaceAction(row))

  private def findAction(key: TaskDAO.Key) =
    quote {
      PublicSchema.TaskDao.query.filter(a => a.id == lift(key.projectId.uuid) && a.projectId == lift(key.uuid))
    }

  private def insertAction(row: Task): Quoted[ActionReturning[Task, Task]] =
    quote {
      PublicSchema.TaskDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Task]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.TaskDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: TaskDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: Task) = {
    quote {
      PublicSchema.TaskDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id, _.projectId)(
          (t, e) => t.id -> e.id,
          (t, e) => t.projectId -> e.projectId,
          (t, e) => t.projectReferenceId -> e.projectReferenceId,
          (t, e) => t.name -> e.name,
          (t, e) => t.unit -> e.unit,
          (t, e) => t.kindId -> e.kindId,
          (t, e) => t.reached -> e.reached,
          (t, e) => t.reachable -> e.reachable,
          (t, e) => t.weight -> e.weight
        )
        .returning(x => x)
    }
  }

  def findById[F[_]: Async: ContextShift](key: UUID): F[List[Task]] = {
    findByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByIdC(key: UUID): ConnectionIO[List[Task]] = {
    run(findByIdAction(key))
  }

  def deleteById[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByIdAction(key))
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Task]] = {
    findByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectIdC(key: UUID): ConnectionIO[List[Task]] = {
    run(findByProjectIdAction(key))
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectIdAction(key))
  }

  private def findByIdAction(key: UUID) = {
    quote {
      PublicSchema.TaskDao.query.filter(a => a.id == lift(key))
    }
  }

  private def deleteByIdAction(key: UUID) = {
    quote {
      findByIdAction(key).delete
    }
  }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.TaskDao.query.filter(a => a.projectId == lift(key))
    }
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

}

object TaskDAO { type Key = TaskId }
