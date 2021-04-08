package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectWriteAccessDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[ProjectWriteAccess]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[ProjectWriteAccess]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: ProjectWriteAccess): F[ProjectWriteAccess] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: ProjectWriteAccess): ConnectionIO[ProjectWriteAccess] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectWriteAccess]): F[List[ProjectWriteAccess]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[ProjectWriteAccess]): ConnectionIO[List[ProjectWriteAccess]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[ProjectWriteAccess] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[ProjectWriteAccess] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: ProjectWriteAccess): F[ProjectWriteAccess] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: ProjectWriteAccess): ConnectionIO[ProjectWriteAccess] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.ProjectWriteAccessDao.query.filter(a => a.projectId == lift(key))
    }

  private def insertAction(row: ProjectWriteAccess): Quoted[ActionReturning[ProjectWriteAccess, ProjectWriteAccess]] =
    quote {
      PublicSchema.ProjectWriteAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectWriteAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectWriteAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectWriteAccess) = {
    quote {
      PublicSchema.ProjectWriteAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId)((t, e) => t.projectId -> e.projectId)
        .returning(x => x)
    }
  }

}
