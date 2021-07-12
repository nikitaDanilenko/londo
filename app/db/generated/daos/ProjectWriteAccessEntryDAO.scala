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

class ProjectWriteAccessEntryDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[ProjectWriteAccessEntry, ProjectWriteAccessEntryDAO.Key] {
  import dbContext._

  override def findC(key: ProjectWriteAccessEntryDAO.Key): ConnectionIO[Option[ProjectWriteAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: ProjectWriteAccessEntry): ConnectionIO[ProjectWriteAccessEntry] = run(insertAction(row))

  override def insertAllC(rows: Seq[ProjectWriteAccessEntry]): ConnectionIO[List[ProjectWriteAccessEntry]] =
    run(insertAllAction(rows))

  override def deleteC(key: ProjectWriteAccessEntryDAO.Key): ConnectionIO[ProjectWriteAccessEntry] =
    run(deleteAction(key))

  override def replaceC(row: ProjectWriteAccessEntry): ConnectionIO[ProjectWriteAccessEntry] = run(replaceAction(row))

  private def findAction(key: ProjectWriteAccessEntryDAO.Key) =
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

  private def deleteAction(key: ProjectWriteAccessEntryDAO.Key) =
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
    findByProjectWriteAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectWriteAccessIdC(key: UUID): ConnectionIO[List[ProjectWriteAccessEntry]] = {
    run(findByProjectWriteAccessIdAction(key))
  }

  def deleteByProjectWriteAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectWriteAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectWriteAccessIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectWriteAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectWriteAccessEntry]] = {
    findByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdC(key: UUID): ConnectionIO[List[ProjectWriteAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdC(key: UUID): ConnectionIO[Long] = {
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

object ProjectWriteAccessEntryDAO { type Key = ProjectWriteAccessEntryId }
