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

class LoginAttemptDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[LoginAttempt, LoginAttemptDAO.Key] {
  import dbContext._

  override def findC(key: LoginAttemptDAO.Key): ConnectionIO[Option[LoginAttempt]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: LoginAttempt): ConnectionIO[LoginAttempt] = run(insertAction(row))
  override def insertAllC(rows: Seq[LoginAttempt]): ConnectionIO[List[LoginAttempt]] = run(insertAllAction(rows))
  override def deleteC(key: LoginAttemptDAO.Key): ConnectionIO[LoginAttempt] = run(deleteAction(key))
  override def replaceC(row: LoginAttempt): ConnectionIO[LoginAttempt] = run(replaceAction(row))

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
