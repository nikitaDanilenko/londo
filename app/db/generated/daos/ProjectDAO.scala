package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[Project]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: Project): F[Project] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Project]): F[List[Project]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[Project] = run(deleteAction(key)).transact(transactor[F])

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

  private def findByOwnerIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.ownerId == lift(key))
    }
  }

  def findByOwnerId[F[_]: Async: ContextShift](key: UUID): F[List[Project]] = {
    run(findByOwnerIdAction(key)).transact(transactor[F])
  }

  private def deleteByOwnerIdAction(key: UUID) = {
    quote {
      findByOwnerIdAction(key).delete
    }
  }

  def deleteByOwnerId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByOwnerIdAction(key)).transact(transactor[F])
  }

  private def findByNameAction(key: String) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.name == lift(key))
    }
  }

  def findByName[F[_]: Async: ContextShift](key: String): F[List[Project]] = {
    run(findByNameAction(key)).transact(transactor[F])
  }

  private def deleteByNameAction(key: String) = {
    quote {
      findByNameAction(key).delete
    }
  }

  def deleteByName[F[_]: Async: ContextShift](key: String): F[Long] = {
    run(deleteByNameAction(key)).transact(transactor[F])
  }

  private def findByParentProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectDao.query.filter(a => a.parentProjectId.contains(lift(key)))
    }
  }

  def findByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[List[Project]] = {
    run(findByParentProjectIdAction(key)).transact(transactor[F])
  }

  private def deleteByParentProjectIdAction(key: UUID) = {
    quote {
      findByParentProjectIdAction(key).delete
    }
  }

  def deleteByParentProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByParentProjectIdAction(key)).transact(transactor[F])
  }

}
