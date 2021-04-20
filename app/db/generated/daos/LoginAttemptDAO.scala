package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class LoginAttemptDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UserId): F[Option[LoginAttempt]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UserId): ConnectionIO[Option[LoginAttempt]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: LoginAttempt): F[LoginAttempt] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: LoginAttempt): ConnectionIO[LoginAttempt] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[LoginAttempt]): F[List[LoginAttempt]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[LoginAttempt]): ConnectionIO[List[LoginAttempt]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UserId): F[LoginAttempt] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UserId): ConnectionIO[LoginAttempt] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: LoginAttempt): F[LoginAttempt] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: LoginAttempt): ConnectionIO[LoginAttempt] = run(replaceAction(row))

  private def findAction(key: UserId) =
    quote {
      PublicSchema.LoginAttemptDao.query.filter(a => a.userId == lift(key.uuid))
    }

  private def insertAction(row: LoginAttempt): Quoted[ActionReturning[LoginAttempt, LoginAttempt]] =
    quote {
      PublicSchema.LoginAttemptDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[LoginAttempt]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.LoginAttemptDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UserId) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: LoginAttempt) = {
    quote {
      PublicSchema.LoginAttemptDao.query
        .insert(lift(row))
        .onConflictUpdate(_.userId)(
          (t, e) => t.userId -> e.userId,
          (t, e) => t.failedAttemptsSinceLastSuccessfulLogin -> e.failedAttemptsSinceLastSuccessfulLogin,
          (t, e) => t.lastSuccessfulLogin -> e.lastSuccessfulLogin
        )
        .returning(x => x)
    }
  }

}
