package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[Dashboard]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[Dashboard]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: Dashboard): F[Dashboard] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: Dashboard): ConnectionIO[Dashboard] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Dashboard]): F[List[Dashboard]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[Dashboard]): ConnectionIO[List[Dashboard]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[Dashboard] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[Dashboard] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: Dashboard): F[Dashboard] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: Dashboard): ConnectionIO[Dashboard] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.DashboardDao.query.filter(a => a.id == lift(key))
    }

  private def insertAction(row: Dashboard): Quoted[ActionReturning[Dashboard, Dashboard]] =
    quote {
      PublicSchema.DashboardDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[Dashboard]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.DashboardDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: Dashboard) = {
    quote {
      PublicSchema.DashboardDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.userId -> e.userId,
          (t, e) => t.header -> e.header,
          (t, e) => t.description -> e.description
        )
        .returning(x => x)
    }
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[Dashboard]] = {
    findByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByUserIdF(key: UUID): ConnectionIO[List[Dashboard]] = {
    run(findByUserIdAction(key))
  }

  def deleteByUserId[F[_]: Async: ContextShift](key: UUID): F[Long] = {
    deleteByUserIdF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByUserIdF(key: UUID): ConnectionIO[Long] = {
    run(deleteByUserIdAction(key))
  }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardDao.query.filter(a => a.userId == lift(key))
    }
  }

  private def deleteByUserIdAction(key: UUID) = {
    quote {
      findByUserIdAction(key).delete
    }
  }

}
