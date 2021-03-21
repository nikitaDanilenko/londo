package daos

import cats.effect.{ Async, ContextShift }
import db.{ DbContext, models }
import doobie.implicits._
import io.getquill.ActionReturning

import java.util.UUID
import javax.inject.Inject

class UserDAO @Inject() (val dbContext: DbContext) {

  import dbContext.PublicSchema.UserDao
  import dbContext._

  def find[F[_]: Async: ContextShift](userId: UUID): F[Option[models.User]] =
    run(findAction(userId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](userIds: Seq[UUID]): F[List[models.User]] =
    run(findAllAction(userIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: models.User
  ): F[models.User] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[models.User]): F[List[models.User]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](userId: UUID): F[models.User] =
    run(deleteAction(userId)).transact(transactor[F])

  def deleteAll[F[_]: Async: ContextShift](userIds: Seq[UUID]): F[Long] =
    run(deleteAllAction(userIds)).transact(transactor[F])

  private def findAction(userId: UUID) =
    quote {
      UserDao.query.filter(_.id == lift(userId))
    }

  private def findAllAction(userIds: Seq[UUID]) = {
    val idSet = userIds.toSet
    quote {
      UserDao.query.filter(user => liftQuery(idSet).contains(user.id))
    }
  }

  private def insertAction(row: models.User): Quoted[ActionReturning[models.User, models.User]] =
    quote {
      UserDao.query
        .insert(lift(row))
        .returning(x => x)
    }

  private def insertAllAction(rows: Seq[models.User]) =
    quote {
      liftQuery(rows).foreach(e => UserDao.query.insert(e).returning(x => x))
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
