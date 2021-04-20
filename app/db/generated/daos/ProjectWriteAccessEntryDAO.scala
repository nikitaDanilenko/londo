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

class ProjectWriteAccessEntryDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: ProjectWriteAccessEntryId): F[Option[ProjectWriteAccessEntry]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: ProjectWriteAccessEntryId): ConnectionIO[Option[ProjectWriteAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: ProjectWriteAccessEntry): F[ProjectWriteAccessEntry] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: ProjectWriteAccessEntry): ConnectionIO[ProjectWriteAccessEntry] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectWriteAccessEntry]): F[List[ProjectWriteAccessEntry]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[ProjectWriteAccessEntry]): ConnectionIO[List[ProjectWriteAccessEntry]] =
    run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: ProjectWriteAccessEntryId): F[ProjectWriteAccessEntry] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: ProjectWriteAccessEntryId): ConnectionIO[ProjectWriteAccessEntry] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: ProjectWriteAccessEntry): F[ProjectWriteAccessEntry] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: ProjectWriteAccessEntry): ConnectionIO[ProjectWriteAccessEntry] = run(replaceAction(row))

  private def findAction(key: ProjectWriteAccessEntryId) =
    quote {
      PublicSchema.ProjectWriteAccessEntryDao.query.filter(a =>
        a.projectWriteAccessId == lift(key.projectWriteAccessId.uuid) && a.userId == lift(key.userId.uuid)
      )
    }

  private def insertAction(
      row: ProjectWriteAccessEntry
  ): Quoted[ActionReturning[ProjectWriteAccessEntry, ProjectWriteAccessEntry]] =
    quote {
      PublicSchema.ProjectWriteAccessEntryDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectWriteAccessEntry]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectWriteAccessEntryDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: ProjectWriteAccessEntryId) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectWriteAccessEntry) = {
    quote {
      PublicSchema.ProjectWriteAccessEntryDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectWriteAccessId, _.userId)(
          (t, e) => t.projectWriteAccessId -> e.projectWriteAccessId,
          (t, e) => t.userId -> e.userId
        )
        .returning(x => x)
    }
  }

  def findByProjectWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectWriteAccessEntry]] = {
    findByProjectWriteAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectWriteAccessIdF(key: UUID): ConnectionIO[List[ProjectWriteAccessEntry]] = {
    run(findByProjectWriteAccessIdAction(key))
  }

  def deleteByProjectWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectWriteAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectWriteAccessIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectWriteAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectWriteAccessEntry]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[ProjectWriteAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByProjectWriteAccessIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectWriteAccessEntryDao.query.filter(a => a.projectWriteAccessId == lift(key))
    }
  }

  private def deleteByProjectWriteAccessIdAction(key: UUID) = {
    quote {
      findByProjectWriteAccessIdAction(key).delete
    }
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectWriteAccessEntryDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
