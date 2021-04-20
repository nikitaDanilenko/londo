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

class ProjectReadAccessDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: ProjectReadAccessId): F[Option[ProjectReadAccess]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: ProjectReadAccessId): ConnectionIO[Option[ProjectReadAccess]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: ProjectReadAccess): F[ProjectReadAccess] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: ProjectReadAccess): ConnectionIO[ProjectReadAccess] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectReadAccess]): F[List[ProjectReadAccess]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[ProjectReadAccess]): ConnectionIO[List[ProjectReadAccess]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: ProjectReadAccessId): F[ProjectReadAccess] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: ProjectReadAccessId): ConnectionIO[ProjectReadAccess] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: ProjectReadAccess): F[ProjectReadAccess] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: ProjectReadAccess): ConnectionIO[ProjectReadAccess] = run(replaceAction(row))

  private def findAction(key: ProjectReadAccessId) =
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

  private def deleteAction(key: ProjectReadAccessId) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectReadAccess) = {
    quote {
      PublicSchema.ProjectReadAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId)((t, e) => t.projectId -> e.projectId)
        .returning(x => x)
    }
  }

}
