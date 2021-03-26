package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class DashboardDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[Dashboard]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: Dashboard): F[Dashboard] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Dashboard]): F[List[Dashboard]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[Dashboard] = run(deleteAction(key)).transact(transactor[F])
  def replace[F[_]: Async: ContextShift](row: Dashboard): F[Dashboard] = run(replaceAction(row)).transact(transactor[F])

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

  private def replaceAction(row: Dashboard) =
    quote {
      PublicSchema.DashboardDao.query.insert(lift(row)).onConflictUpdate(_.id)((t, e) => t -> e).returning(x => x)
    }

  private def findByUserIdAction(key: UUID) = {
    quote {
      PublicSchema.DashboardDao.query.filter(a => a.userId == lift(key))
    }
  }

  def findByUserId[F[_]: Async: ContextShift](key: UUID): F[List[Dashboard]] = {
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
