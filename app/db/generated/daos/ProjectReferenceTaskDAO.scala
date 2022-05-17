package db.generated.daos

import cats.effect.{ Async, ContextShift }
import cats.syntax.applicativeError._
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectReferenceTaskDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[ProjectReferenceTask, ProjectReferenceTaskDAO.Key] {
  import dbContext._

  override def findC(key: ProjectReferenceTaskDAO.Key): ConnectionIO[Option[ProjectReferenceTask]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: ProjectReferenceTask): ConnectionIO[Either[Throwable, ProjectReferenceTask]] =
    run(insertAction(row)).attempt

  override def insertAllC(
      rows: Seq[ProjectReferenceTask]
  ): ConnectionIO[Either[Throwable, List[ProjectReferenceTask]]] = run(insertAllAction(rows)).attempt

  override def deleteC(key: ProjectReferenceTaskDAO.Key): ConnectionIO[Either[Throwable, ProjectReferenceTask]] =
    run(deleteAction(key)).attempt

  override def replaceC(row: ProjectReferenceTask): ConnectionIO[Either[Throwable, ProjectReferenceTask]] =
    run(replaceAction(row)).attempt

  private def findAction(key: ProjectReferenceTaskDAO.Key) =
    quote {
      PublicSchema.ProjectReferenceTaskDao.query.filter(a =>
        a.projectId == lift(key.projectId.uuid) && a.id == lift(key.uuid)
      )
    }

  private def insertAction(
      row: ProjectReferenceTask
  ): Quoted[ActionReturning[ProjectReferenceTask, ProjectReferenceTask]] =
    quote {
      PublicSchema.ProjectReferenceTaskDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectReferenceTask]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectReferenceTaskDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: ProjectReferenceTaskDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: ProjectReferenceTask) = {
    quote {
      PublicSchema.ProjectReferenceTaskDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId, _.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.projectId -> e.projectId,
          (t, e) => t.projectReferenceId -> e.projectReferenceId,
          (t, e) => t.weight -> e.weight
        )
        .returning(x => x)
    }
  }

  def findById[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReferenceTask]] = {
    findByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByIdC(key: UUID): ConnectionIO[List[ProjectReferenceTask]] = {
    run(findByIdAction(key))
  }

  def deleteById[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByIdAction(key)).attempt
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectReferenceTask]] = {
    findByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectIdC(key: UUID): ConnectionIO[List[ProjectReferenceTask]] = {
    run(findByProjectIdAction(key))
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByProjectIdAction(key)).attempt
  }

  private def findByIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectReferenceTaskDao.query.filter(a => a.id == lift(key))
    }
  }

  private def deleteByIdAction(key: UUID) = {
    quote {
      findByIdAction(key).delete
    }
  }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectReferenceTaskDao.query.filter(a => a.projectId == lift(key))
    }
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

}

object ProjectReferenceTaskDAO { type Key = TaskId }
