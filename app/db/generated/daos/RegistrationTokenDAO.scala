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

class RegistrationTokenDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[RegistrationToken, RegistrationTokenDAO.Key] {
  import dbContext._

  override def findC(key: RegistrationTokenDAO.Key): ConnectionIO[Option[RegistrationToken]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: RegistrationToken): ConnectionIO[RegistrationToken] = run(insertAction(row))

  override def insertAllC(rows: Seq[RegistrationToken]): ConnectionIO[List[RegistrationToken]] =
    run(insertAllAction(rows))

  override def deleteC(key: RegistrationTokenDAO.Key): ConnectionIO[RegistrationToken] = run(deleteAction(key))
  override def replaceC(row: RegistrationToken): ConnectionIO[RegistrationToken] = run(replaceAction(row))

  private def findAction(key: RegistrationTokenDAO.Key) =
    quote {
      PublicSchema.RegistrationTokenDao.query.filter(a => a.email == lift(key.email))
    }

  private def insertAction(row: RegistrationToken): Quoted[ActionReturning[RegistrationToken, RegistrationToken]] =
    quote {
      PublicSchema.RegistrationTokenDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[RegistrationToken]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.RegistrationTokenDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: RegistrationTokenDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: RegistrationToken) = {
    quote {
      PublicSchema.RegistrationTokenDao.query
        .insert(lift(row))
        .onConflictUpdate(_.email)((t, e) => t.email -> e.email, (t, e) => t.token -> e.token)
        .returning(x => x)
    }
  }

}

object RegistrationTokenDAO { type Key = RegistrationTokenId }
