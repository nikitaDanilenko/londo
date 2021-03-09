package services.user

import cats.effect.{ Async, ContextShift }
import db.{ DbContext, models }
import doobie.implicits._
import io.getquill.ActionReturning

import javax.inject.Inject

class UserDTO @Inject() (val dbContext: DbContext) { self =>

  import dbContext.PublicSchema.UserDao
  import dbContext._

  def find[F[_]: Async: ContextShift](userId: UserId): F[Option[models.User]] =
    run(findAction(userId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](userIds: Seq[UserId]): F[List[models.User]] =
    run(findAllAction(userIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: models.User
  ): F[models.User] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[models.User]): F[List[models.User]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](userId: UserId): F[models.User] =
    run(deleteAction(userId)).transact(transactor[F])

  def deleteAll[F[_]: Async: ContextShift](userIds: Seq[UserId]): F[Long] =
    run(deleteAllAction(userIds)).transact(transactor[F])

  private def findAction(userId: UserId) =
    quote {
      UserDao.query.filter(_.id == lift(userId.uuid))
    }

  private def findAllAction(userIds: Seq[UserId]) = {
    val idSet = userIds.map(_.uuid).toSet
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

  private def deleteAction(userId: UserId) =
    quote {
      findAction(userId).delete
        .returning(x => x)
    }

  private def deleteAllAction(userIds: Seq[UserId]) =
    quote {
      findAllAction(userIds).delete
    }

}
