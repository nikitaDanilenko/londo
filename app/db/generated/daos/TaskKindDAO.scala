package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class TaskKindDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[TaskKind]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: TaskKind): F[TaskKind] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[TaskKind]): F[List[TaskKind]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[TaskKind] = run(deleteAction(key)).transact(transactor[F])

  private def findAction(key: UUID) =
    quote {
      PublicSchema.TaskKindDao.query.filter(a => a.id == lift(key))
    }

  private def insertAction(row: TaskKind): Quoted[ActionReturning[TaskKind, TaskKind]] =
    quote {
      PublicSchema.TaskKindDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[TaskKind]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.TaskKindDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

}
