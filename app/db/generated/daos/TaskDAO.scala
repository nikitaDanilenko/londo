package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class TaskDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[Task]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: Task): F[Task] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Task]): F[List[Task]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Task] = run(deleteAction(key)).transact(transactor[F])

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

  private def findByIdAction(key: UUID) = {
    quote {
      PublicSchema.TaskDao.query.filter(a => a.id == lift(key))
    }
  }

  def findById[F[_]: Async: ContextShift](key: UUID): F[List[Task]] = {
    run(findByIdAction(key)).transact(transactor[F])
  }

  private def deleteByIdAction(key: UUID) = {
    quote {
      findByIdAction(key).delete
    }
  }

  def deleteById[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByIdAction(key)).transact(transactor[F])
  }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.TaskDao.query.filter(a => a.projectId == lift(key))
    }
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Task]] = {
    run(findByProjectIdAction(key)).transact(transactor[F])
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByProjectIdAction(key)).transact(transactor[F])
  }

}
