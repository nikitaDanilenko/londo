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

class UserSettingsDAO @Inject() (
    dbContext: DbContext,
    override protected val dbTransactorProvider: DbTransactorProvider
) extends DAOFunctions[UserSettings, UserSettingsDAO.Key] {
  import dbContext._

  override def findC(key: UserSettingsDAO.Key): ConnectionIO[Option[UserSettings]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: UserSettings): ConnectionIO[Either[Throwable, UserSettings]] =
    run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[UserSettings]): ConnectionIO[Either[Throwable, List[UserSettings]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: UserSettingsDAO.Key): ConnectionIO[Either[Throwable, UserSettings]] =
    run(deleteAction(key)).attempt

  override def replaceC(row: UserSettings): ConnectionIO[Either[Throwable, UserSettings]] =
    run(replaceAction(row)).attempt

  private def findAction(key: UserSettingsDAO.Key) =
    quote {
      PublicSchema.UserSettingsDao.query.filter(a => a.userId == lift(key.uuid))
    }

  private def insertAction(row: UserSettings): Quoted[ActionReturning[UserSettings, UserSettings]] =
    quote {
      PublicSchema.UserSettingsDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[UserSettings]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserSettingsDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UserSettingsDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: UserSettings) = {
    quote {
      PublicSchema.UserSettingsDao.query
        .insert(lift(row))
        .onConflictUpdate(_.userId)((t, e) => t.darkMode -> e.darkMode)
        .returning(x => x)
    }
  }

}

object UserSettingsDAO { type Key = UserId }
