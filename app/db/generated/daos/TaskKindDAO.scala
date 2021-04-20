package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class TaskKindDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: TaskKindId): F[Option[TaskKind]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: TaskKindId): ConnectionIO[Option[TaskKind]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: TaskKind): F[TaskKind] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: TaskKind): ConnectionIO[TaskKind] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[TaskKind]): F[List[TaskKind]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[TaskKind]): ConnectionIO[List[TaskKind]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: TaskKindId): F[TaskKind] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: TaskKindId): ConnectionIO[TaskKind] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: TaskKind): F[TaskKind] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: TaskKind): ConnectionIO[TaskKind] = run(replaceAction(row))

  private def findAction(key: TaskKindId) =
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

  private def deleteAction(key: TaskKindId) =
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
