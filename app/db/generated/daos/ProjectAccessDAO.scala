package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class ProjectAccessDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: (UUID, UUID)): F[Option[ProjectAccess]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: (UUID, UUID)): ConnectionIO[Option[ProjectAccess]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: ProjectAccess): F[ProjectAccess] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: ProjectAccess): ConnectionIO[ProjectAccess] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[ProjectAccess]): F[List[ProjectAccess]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[ProjectAccess]): ConnectionIO[List[ProjectAccess]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: (UUID, UUID)): F[ProjectAccess] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: (UUID, UUID)): ConnectionIO[ProjectAccess] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: ProjectAccess): F[ProjectAccess] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: ProjectAccess): ConnectionIO[ProjectAccess] = run(replaceAction(row))

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

  private def replaceAction(row: ProjectAccess) = {
    quote {
      PublicSchema.ProjectAccessDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId, _.userId)((t, e) => t.projectId -> e.projectId, (t, e) => t.userId -> e.userId)
        .returning(x => x)
    }
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectAccess]] = {
    findByProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectIdF(key: UUID): ConnectionIO[List[ProjectAccess]] = {
    run(findByProjectIdAction(key))
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByProjectIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByProjectIdAction(key))
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[ProjectAccess]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[ProjectAccess]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectAccessDao.query.filter(a => a.projectId == lift(key))
    }
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.ProjectAccessDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
