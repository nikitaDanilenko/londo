package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[User, UserDAO.Key] {
  import dbContext._
  override def findC(key: UserDAO.Key): ConnectionIO[Option[User]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: User): ConnectionIO[User] = run(insertAction(row))
  override def insertAllC(rows: Seq[User]): ConnectionIO[List[User]] = run(insertAllAction(rows))
  override def deleteC(key: UserDAO.Key): ConnectionIO[User] = run(deleteAction(key))
  override def replaceC(row: User): ConnectionIO[User] = run(replaceAction(row))

  private def findAction(key: UserDAO.Key) =
    quote {
      PublicSchema.UserDao.query.filter(a => a.id == lift(key.uuid))
    }

  private def insertAction(row: User): Quoted[ActionReturning[User, User]] =
    quote {
      PublicSchema.UserDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[User]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UserDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: User) = {
    quote {
      PublicSchema.UserDao.query
        .insert(lift(row))
        .onConflictUpdate(_.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.nickname -> e.nickname,
          (t, e) => t.email -> e.email,
          (t, e) => t.passwordSalt -> e.passwordSalt,
          (t, e) => t.passwordHash -> e.passwordHash,
          (t, e) => t.iterations -> e.iterations
        )
        .returning(x => x)
    }
  }

  def findByEmail[F[_]: Async: ContextShift](key: String): F[List[User]] = {
    findByEmailC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByEmailC(key: String): ConnectionIO[List[User]] = {
    run(findByEmailAction(key))
  }

  def deleteByEmail[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByEmailC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByEmailC(key: String): ConnectionIO[Long] = {
    run(deleteByEmailAction(key))
  }

  def findByNickname[F[_]: Async: ContextShift](key: String): F[List[User]] = {
    findByNicknameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByNicknameC(key: String): ConnectionIO[List[User]] = {
    run(findByNicknameAction(key))
  }

  def deleteByNickname[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByNicknameC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByNicknameC(key: String): ConnectionIO[Long] = {
    run(deleteByNicknameAction(key))
  }

  private def findByEmailAction(key: String) = {
    quote {
      PublicSchema.UserDao.query.filter(a => a.email == lift(key))
    }
  }

  private def deleteByEmailAction(key: String) = {
    quote {
      findByEmailAction(key).delete
    }
  }

  private def findByNicknameAction(key: String) = {
    quote {
      PublicSchema.UserDao.query.filter(a => a.nickname == lift(key))
    }
  }

  private def deleteByNicknameAction(key: String) = {
    quote {
      findByNicknameAction(key).delete
    }
  }

}

object UserDAO { type Key = UserId }
