package daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models.User
import doobie.implicits._
import io.getquill.ActionReturning

import java.util.UUID
import javax.inject.Inject

class UserDAO @Inject() (val dbContext: DbContext) {

  import dbContext.PublicSchema.UserDao
  import dbContext._

  def find[F[_]: Async: ContextShift](userId: UUID): F[Option[User]] =
    run(findAction(userId)).map(_.headOption).transact(transactor[F])

  def findAll[F[_]: Async: ContextShift](userIds: Seq[UUID]): F[List[User]] =
    run(findAllAction(userIds)).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](
      row: User
  ): F[User] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[User]): F[List[User]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](userId: UUID): F[User] =
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

  private def insertAction(row: User): Quoted[ActionReturning[User, User]] =
    quote {
      UserDao.query
        .insert(lift(row))
        .returning(x => x)
    }

  private def insertAllAction(rows: Seq[User]) =
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
