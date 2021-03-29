package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[User]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[User]] = run(findAction(key)).map(_.headOption)
  def insert[F[_]: Async: ContextShift](row: User): F[User] = insertF(row).transact(dbTransactorProvider.transactor[F])
  def insertF(row: User): ConnectionIO[User] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[User]): F[List[User]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[User]): ConnectionIO[List[User]] = run(insertAllAction(rows))
  def delete[F[_]: Async: ContextShift](key: UUID): F[User] = deleteF(key).transact(dbTransactorProvider.transactor[F])
  def deleteF(key: UUID): ConnectionIO[User] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: User): F[User] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: User): ConnectionIO[User] = run(replaceAction(row))

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
    findByEmailF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByEmailF(key: String): ConnectionIO[List[User]] = {
    run(findByEmailAction(key))
  }

  def deleteByEmail[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByEmailF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByEmailF(key: String): ConnectionIO[Long] = {
    run(deleteByEmailAction(key))
  }

  def findByNickname[F[_]: Async: ContextShift](key: String): F[List[User]] = {
    findByNicknameF(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByNicknameF(key: String): ConnectionIO[List[User]] = {
    run(findByNicknameAction(key))
  }

  def deleteByNickname[F[_]: Async: ContextShift](key: String): F[Long] = {
    deleteByNicknameF(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByNicknameF(key: String): ConnectionIO[Long] = {
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
