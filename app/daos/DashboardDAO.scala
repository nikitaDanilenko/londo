package daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models.Dashboard
import doobie.implicits._
import io.getquill.ActionReturning

import java.util.UUID
import javax.inject.Inject

class DashboardDAO @Inject() (val dbContext: DbContext) {

  import dbContext.PublicSchema.DashboardDao
  import dbContext._

  def find[F[_]: Async: ContextShift](userId: UUID): F[Option[Dashboard]] =
    run(findAction(userId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](userIds: Seq[UUID]): F[List[Dashboard]] =
    run(findAllAction(userIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: Dashboard
  ): F[Dashboard] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[Dashboard]): F[List[Dashboard]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](userId: UUID): F[Dashboard] =
    run(deleteAction(userId)).transact(transactor[F])

  def deleteAll[F[_]: Async: ContextShift](userIds: Seq[UUID]): F[Long] =
    run(deleteAllAction(userIds)).transact(transactor[F])

  private def findAction(userId: UUID) =
    quote {
      DashboardDao.query.filter(_.id == lift(userId))
    }

  private def findAllAction(userIds: Seq[UUID]) = {
    val idSet = userIds.toSet
    quote {
      DashboardDao.query.filter(user => liftQuery(idSet).contains(user.id))
    }
  }

  private def insertAction(row: Dashboard): Quoted[ActionReturning[Dashboard, Dashboard]] =
    quote {
      DashboardDao.query
        .insert(lift(row))
        .returning(x => x)
    }

  private def insertAllAction(rows: Seq[Dashboard]) =
    quote {
      liftQuery(rows).foreach(e => DashboardDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(userId: UUID) =
    quote {
      findAction(userId).delete
        .returning(x => x)
    }

  private def deleteAllAction(userIds: Seq[UUID]) =
    quote {
      findAllAction(userIds).delete
    }

}
