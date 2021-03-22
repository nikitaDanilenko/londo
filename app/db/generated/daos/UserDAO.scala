package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[User]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: User): F[User] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[User]): F[List[User]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[User] = run(deleteAction(key)).transact(transactor[F])

  private def findAction(key: UUID) =
    quote {
      PublicSchema.UserDao.query.filter(a => a.id == lift(key))
    }

  private def insertAction(row: User): Quoted[ActionReturning[User, User]] =
    quote {
      PublicSchema.UserDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[User]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def findByEmailAction(key: String) = {
    quote {
      PublicSchema.UserDao.query.filter(a => a.email == lift(key))
    }
  }

  def findByEmail[F[_]: Async: ContextShift](key: String): F[List[User]] = {
    run(findByEmailAction(key)).transact(transactor[F])
  }

  private def deleteByEmailAction(key: String) = {
    quote {
      findByEmailAction(key).delete
    }
  }

  def deleteByEmail[F[_]: Async: ContextShift](key: String): F[Long] = {
    run(deleteByEmailAction(key)).transact(transactor[F])
  }

  private def findByNicknameAction(key: String) = {
    quote {
      PublicSchema.UserDao.query.filter(a => a.nickname == lift(key))
    }
  }

  def findByNickname[F[_]: Async: ContextShift](key: String): F[List[User]] = {
    run(findByNicknameAction(key)).transact(transactor[F])
  }

  private def deleteByNicknameAction(key: String) = {
    quote {
      findByNicknameAction(key).delete
    }
  }

  def deleteByNickname[F[_]: Async: ContextShift](key: String): F[Long] = {
    run(deleteByNicknameAction(key)).transact(transactor[F])
  }

}
