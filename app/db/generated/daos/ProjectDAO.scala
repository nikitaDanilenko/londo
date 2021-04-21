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

class ProjectDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[Project, ProjectDAO.Key] {
  import dbContext._
  override def findC(key: ProjectDAO.Key): ConnectionIO[Option[Project]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: Project): ConnectionIO[Project] = run(insertAction(row))
  override def insertAllC(rows: Seq[Project]): ConnectionIO[List[Project]] = run(insertAllAction(rows))
  override def deleteC(key: ProjectDAO.Key): ConnectionIO[Project] = run(deleteAction(key))
  override def replaceC(row: Project): ConnectionIO[Project] = run(replaceAction(row))

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
          (t, e) => t.parentProjectId -> e.parentProjectId,
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

  def deleteByOwnerId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByOwnerIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByOwnerIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByOwnerIdAction(key))
  }

  def findByName[F[_]: Async: ContextShift](key: String): F[List[Project]] = {
    findByNameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByNameC(key: String): ConnectionIO[List[Project]] = {
    run(findByNameAction(key))
  }

  def deleteByName[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByNameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByNameC(key: String): ConnectionIO[Long] = {
    run(deleteByNameAction(key))
  }

  def findByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Project]] = {
    findByParentProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByParentProjectIdC(key: UUID): ConnectionIO[List[Project]] = {
    run(findByParentProjectIdAction(key))
  }

  def deleteByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByParentProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByParentProjectIdC(key: UUID): ConnectionIO[Long] = {
    run(deleteByParentProjectIdAction(key))
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

  private def findByParentProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.parentProjectId.contains(lift(key)))
    }
  }

  private def deleteByParentProjectIdAction(key: UUID) = {
    quote {
      findByParentProjectIdAction(key).delete
    }
  }

}

object ProjectDAO { type Key = ProjectId }
