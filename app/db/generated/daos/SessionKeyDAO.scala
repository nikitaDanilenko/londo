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

class SessionKeyDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[SessionKey, SessionKeyDAO.Key] {
  import dbContext._
  override def findC(key: SessionKeyDAO.Key): ConnectionIO[Option[SessionKey]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: SessionKey): ConnectionIO[SessionKey] = run(insertAction(row))
  override def insertAllC(rows: Seq[SessionKey]): ConnectionIO[List[SessionKey]] = run(insertAllAction(rows))
  override def deleteC(key: SessionKeyDAO.Key): ConnectionIO[SessionKey] = run(deleteAction(key))
  override def replaceC(row: SessionKey): ConnectionIO[SessionKey] = run(replaceAction(row))

  private def findAction(key: SessionKeyDAO.Key) =
    quote {
      PublicSchema.SessionKeyDao.query.filter(a => a.userId == lift(key.uuid))
    }

  private def insertAction(row: SessionKey): Quoted[ActionReturning[SessionKey, SessionKey]] =
    quote {
      PublicSchema.SessionKeyDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[SessionKey]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.SessionKeyDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: SessionKeyDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: SessionKey) = {
    quote {
      PublicSchema.SessionKeyDao.query
        .insert(lift(row))
        .onConflictUpdate(_.userId)((t, e) => t.userId -> e.userId, (t, e) => t.publicKey -> e.publicKey)
        .returning(x => x)
    }
  }

}

object SessionKeyDAO { type Key = UserId }
