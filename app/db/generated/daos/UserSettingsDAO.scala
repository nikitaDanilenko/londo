package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserSettingsDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[UserSettings]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[UserSettings]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: UserSettings): F[UserSettings] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: UserSettings): ConnectionIO[UserSettings] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[UserSettings]): F[List[UserSettings]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[UserSettings]): ConnectionIO[List[UserSettings]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[UserSettings] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[UserSettings] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: UserSettings): F[UserSettings] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: UserSettings): ConnectionIO[UserSettings] = run(replaceAction(row))

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

  private def replaceAction(row: UserSettings) =
    quote {
      PublicSchema.UserSettingsDao.query
        .insert(lift(row))
        .onConflictUpdate(_.userId)((t, e) => t -> e)
        .returning(x => x)
    }

}
