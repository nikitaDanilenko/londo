package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[Project]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[Project]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: Project): F[Project] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: Project): ConnectionIO[Project] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Project]): F[List[Project]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[Project]): ConnectionIO[List[Project]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[Project] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[Project] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: Project): F[Project] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: Project): ConnectionIO[Project] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.id == lift(key))
    }

  private def insertAction(row: Project): Quoted[ActionReturning[Project, Project]] =
    quote {
      PublicSchema.ProjectDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Project]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
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
    findByOwnerIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByOwnerIdF(key: UUID): ConnectionIO[List[Project]] = {
    run(findByOwnerIdAction(key))
  }

  def deleteByOwnerId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByOwnerIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByOwnerIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByOwnerIdAction(key))
  }

  def findByName[F[_]: Async: ContextShift](key: String): F[List[Project]] = {
    findByNameF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByNameF(key: String): ConnectionIO[List[Project]] = {
    run(findByNameAction(key))
  }

  def deleteByName[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByNameF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByNameF(key: String): ConnectionIO[Long] = {
    run(deleteByNameAction(key))
  }

  def findByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Project]] = {
    findByParentProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByParentProjectIdF(key: UUID): ConnectionIO[List[Project]] = {
    run(findByParentProjectIdAction(key))
  }

  def deleteByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByParentProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByParentProjectIdF(key: UUID): ConnectionIO[Long] = {
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
