package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserSettingsDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[UserSettings]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: UserSettings): F[UserSettings] =
    run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[UserSettings]): F[List[UserSettings]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[UserSettings] = run(deleteAction(key)).transact(transactor[F])

  private def findAction(key: UUID) =
    quote {
      PublicSchema.UserSettingsDao.query.filter(a => a.userId == lift(key))
    }

  private def insertAction(row: UserSettings): Quoted[ActionReturning[UserSettings, UserSettings]] =
    quote {
      PublicSchema.UserSettingsDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[UserSettings]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserSettingsDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

}
