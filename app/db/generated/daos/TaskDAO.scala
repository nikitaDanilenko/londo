package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class TaskDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[Task]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: (UUID, UUID)): ConnectionIO[Option[Task]] = run(findAction(key)).map(_.headOption)
  def insert[F[_]: Async: ContextShift](row: Task): F[Task] = insertF(row).transact(dbTransactorProvider.transactor[F])
  def insertF(row: Task): ConnectionIO[Task] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Task]): F[List[Task]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[Task]): ConnectionIO[List[Task]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Task] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: (UUID, UUID)): ConnectionIO[Task] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: Task): F[Task] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: Task): ConnectionIO[Task] = run(replaceAction(row))

  private def findAction(key: (UUID, UUID)) =
    quote {
      PublicSchema.TaskDao.query.filter(a => a.id == lift(key._1) && a.projectId == lift(key._2))
    }

  private def insertAction(row: Task): Quoted[ActionReturning[Task, Task]] =
    quote {
      PublicSchema.TaskDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Task]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.TaskDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: (UUID, UUID)) =
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
    findByIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByIdF(key: UUID): ConnectionIO[List[Task]] = {
    run(findByIdAction(key))
  }

  def deleteById[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByIdAction(key))
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Task]] = {
    findByProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectIdF(key: UUID): ConnectionIO[List[Task]] = {
    run(findByProjectIdAction(key))
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectIdF(key: UUID): ConnectionIO[Long] = {
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
