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

class ProjectDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[Project, ProjectDAO.Key] {
  import dbContext._
  override def findC(key: ProjectDAO.Key): ConnectionIO[Option[Project]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: Project): ConnectionIO[Either[Throwable, Project]] = run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[Project]): ConnectionIO[Either[Throwable, List[Project]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: ProjectDAO.Key): ConnectionIO[Either[Throwable, Project]] = run(deleteAction(key)).attempt
  override def replaceC(row: Project): ConnectionIO[Either[Throwable, Project]] = run(replaceAction(row)).attempt

  private def findAction(key: ProjectDAO.Key) =
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.id == lift(key.uuid))
    }

  private def insertAction(row: Project): Quoted[ActionReturning[Project, Project]] =
    quote {
      PublicSchema.ProjectDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Project]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: ProjectDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: Project) = {
    quote {
      PublicSchema.ProjectDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.ownerId -> e.ownerId,
          (t, e) => t.name -> e.name,
          (t, e) => t.description -> e.description,
          (t, e) => t.flatIfSingleTask -> e.flatIfSingleTask
        )
        .returning(x => x)
    }
  }

  def findByOwnerId[F[_]: Async: ContextShift](key: UUID): F[List[Project]] = {
    findByOwnerIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByOwnerIdC(key: UUID): ConnectionIO[List[Project]] = {
    run(findByOwnerIdAction(key))
  }

  def deleteByOwnerId[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByOwnerIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByOwnerIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByOwnerIdAction(key)).attempt
  }

  def findByName[F[_]: Async: ContextShift](key: String): F[List[Project]] = {
    findByNameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByNameC(key: String): ConnectionIO[List[Project]] = {
    run(findByNameAction(key))
  }

  def deleteByName[F[_]: Async: ContextShift](key: String): F[Either[Throwable, Long]] = {
    deleteByNameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByNameC(key: String): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByNameAction(key)).attempt
  }

  private def findByOwnerIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.ownerId == lift(key))
    }
  }

  private def deleteByOwnerIdAction(key: UUID) = {
    quote {
      findByOwnerIdAction(key).delete
    }
  }

  private def findByNameAction(key: String) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.name == lift(key))
    }
  }

  private def deleteByNameAction(key: String) = {
    quote {
      findByNameAction(key).delete
    }
  }

}

object ProjectDAO { type Key = ProjectId }
