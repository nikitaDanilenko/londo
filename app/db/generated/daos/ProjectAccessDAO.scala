package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectAccessDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[ProjectAccess]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: ProjectAccess): F[ProjectAccess] =
    run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectAccess]): F[List[ProjectAccess]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[ProjectAccess] =
    run(deleteAction(key)).transact(transactor[F])

  private def findAction(key: (UUID, UUID)) =
    quote {
      PublicSchema.ProjectAccessDao.query.filter(a => a.projectId == lift(key._1) && a.userId == lift(key._2))
    }

  private def insertAction(row: ProjectAccess): Quoted[ActionReturning[ProjectAccess, ProjectAccess]] =
    quote {
      PublicSchema.ProjectAccessDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[ProjectAccess]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.ProjectAccessDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: (UUID, UUID)) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectAccessDao.query.filter(a => a.projectId == lift(key))
    }
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectAccess]] = {
    run(findByProjectIdAction(key)).transact(transactor[F])
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByProjectIdAction(key)).transact(transactor[F])
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectAccessDao.query.filter(a => a.userId == lift(key))
    }
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectAccess]] = {
    run(findByUserIdAction(key)).transact(transactor[F])
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    run(deleteByUserIdAction(key)).transact(transactor[F])
  }

}
