package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectReadAccessEntryDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[ProjectReadAccessEntry]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: (UUID, UUID)): ConnectionIO[Option[ProjectReadAccessEntry]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: ProjectReadAccessEntry): F[ProjectReadAccessEntry] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: ProjectReadAccessEntry): ConnectionIO[ProjectReadAccessEntry] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectReadAccessEntry]): F[List[ProjectReadAccessEntry]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[ProjectReadAccessEntry]): ConnectionIO[List[ProjectReadAccessEntry]] =
    run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[ProjectReadAccessEntry] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: (UUID, UUID)): ConnectionIO[ProjectReadAccessEntry] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: ProjectReadAccessEntry): F[ProjectReadAccessEntry] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: ProjectReadAccessEntry): ConnectionIO[ProjectReadAccessEntry] = run(replaceAction(row))

  private def findAction(key: (UUID, UUID)) =
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query.filter(a =>
        a.projectReadAccessId == lift(key._1) && a.userId == lift(key._2)
      )
    }

  private def insertAction(
      row: ProjectReadAccessEntry
  ): Quoted[ActionReturning[ProjectReadAccessEntry, ProjectReadAccessEntry]] =
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectReadAccessEntry]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectReadAccessEntryDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: (UUID, UUID)) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectReadAccessEntry) = {
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectReadAccessId, _.userId)(
          (t, e) => t.projectReadAccessId -> e.projectReadAccessId,
          (t, e) => t.userId -> e.userId
        )
        .returning(x => x)
    }
  }

  def findByProjectReadAccessId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReadAccessEntry]] = {
    findByProjectReadAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectReadAccessIdF(key: UUID): ConnectionIO[List[ProjectReadAccessEntry]] = {
    run(findByProjectReadAccessIdAction(key))
  }

  def deleteByProjectReadAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectReadAccessIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectReadAccessIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectReadAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReadAccessEntry]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[ProjectReadAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByProjectReadAccessIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query.filter(a => a.projectReadAccessId == lift(key))
    }
  }

  private def deleteByProjectReadAccessIdAction(key: UUID) = {
    quote {
      findByProjectReadAccessIdAction(key).delete
    }
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
