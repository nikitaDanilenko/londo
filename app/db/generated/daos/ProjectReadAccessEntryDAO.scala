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

class ProjectReadAccessEntryDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[ProjectReadAccessEntry, ProjectReadAccessEntryDAO.Key] {
  import dbContext._

  override def findC(key: ProjectReadAccessEntryDAO.Key): ConnectionIO[Option[ProjectReadAccessEntry]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: ProjectReadAccessEntry): ConnectionIO[ProjectReadAccessEntry] = run(insertAction(row))

  override def insertAllC(rows: Seq[ProjectReadAccessEntry]): ConnectionIO[List[ProjectReadAccessEntry]] =
    run(insertAllAction(rows))

  override def deleteC(key: ProjectReadAccessEntryDAO.Key): ConnectionIO[ProjectReadAccessEntry] =
    run(deleteAction(key))

  override def replaceC(row: ProjectReadAccessEntry): ConnectionIO[ProjectReadAccessEntry] = run(replaceAction(row))

  private def findAction(key: ProjectReadAccessEntryDAO.Key) =
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query.filter(a =>
        a.projectReadAccessId == lift(key.projectReadAccessId.uuid) && a.userId == lift(key.userId.uuid)
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

  private def deleteAction(key: ProjectReadAccessEntryDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectReadAccessEntry) = {
    quote {
      PublicSchema.ProjectReadAccessEntryDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectReadAccessId, _.userId)(
          (t, e) => t.projectReadAccessId -> e.projectReadAccessId,
          (t, e) => t.userId -> e.userId,
          (t, e) => t.hasAccess -> e.hasAccess
        )
        .returning(x => x)
    }
  }

  def findByProjectReadAccessId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReadAccessEntry]] = {
    findByProjectReadAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectReadAccessIdC(key: UUID): ConnectionIO[List[ProjectReadAccessEntry]] = {
    run(findByProjectReadAccessIdAction(key))
  }

  def deleteByProjectReadAccessId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectReadAccessIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectReadAccessIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectReadAccessIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReadAccessEntry]] = {
    findByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdC(key: UUID): ConnectionIO[List[ProjectReadAccessEntry]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdC(key: UUID): ConnectionIO[Long] = {
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

object ProjectReadAccessEntryDAO { type Key = ProjectReadAccessEntryId }
