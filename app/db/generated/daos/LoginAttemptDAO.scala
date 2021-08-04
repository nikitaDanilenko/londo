package db.generated.daos

import cats.effect.{ Async, ContextShift }
import cats.syntax.applicativeError._
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class LoginAttemptDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[LoginAttempt, LoginAttemptDAO.Key] {
  import dbContext._

  override def findC(key: LoginAttemptDAO.Key): ConnectionIO[Option[LoginAttempt]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: LoginAttempt): ConnectionIO[Either[Throwable, LoginAttempt]] =
    run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[LoginAttempt]): ConnectionIO[Either[Throwable, List[LoginAttempt]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: LoginAttemptDAO.Key): ConnectionIO[Either[Throwable, LoginAttempt]] =
    run(deleteAction(key)).attempt

  override def replaceC(row: LoginAttempt): ConnectionIO[Either[Throwable, LoginAttempt]] =
    run(replaceAction(row)).attempt

  private def findAction(key: LoginAttemptDAO.Key) =
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

  private def deleteAction(key: LoginAttemptDAO.Key) =
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

object LoginAttemptDAO { type Key = UserId }
