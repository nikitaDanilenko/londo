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

class UserDetailsDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[UserDetails, UserDetailsDAO.Key] {
  import dbContext._

  override def findC(key: UserDetailsDAO.Key): ConnectionIO[Option[UserDetails]] =
    run(findAction(key)).map(_.headOption)

  override def insertC(row: UserDetails): ConnectionIO[UserDetails] = run(insertAction(row))
  override def insertAllC(rows: Seq[UserDetails]): ConnectionIO[List[UserDetails]] = run(insertAllAction(rows))
  override def deleteC(key: UserDetailsDAO.Key): ConnectionIO[UserDetails] = run(deleteAction(key))
  override def replaceC(row: UserDetails): ConnectionIO[UserDetails] = run(replaceAction(row))

  private def findAction(key: UserDetailsDAO.Key) =
    quote {
      PublicSchema.UserDetailsDao.query.filter(a => a.userId == lift(key.uuid))
    }

  private def insertAction(row: UserDetails): Quoted[ActionReturning[UserDetails, UserDetails]] =
    quote {
      PublicSchema.UserDetailsDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[UserDetails]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserDetailsDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UserDetailsDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: UserDetails) = {
    quote {
      PublicSchema.UserDetailsDao.query
        .insert(lift(row))
        .onConflictUpdate(_.userId)(
          (t, e) => t.userId -> e.userId,
          (t, e) => t.firstName -> e.firstName,
          (t, e) => t.lastName -> e.lastName,
          (t, e) => t.description -> e.description
        )
        .returning(x => x)
    }
  }

}

object UserDetailsDAO { type Key = UserId }
