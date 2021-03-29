package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class RegistrationTokenDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: String): F[Option[RegistrationToken]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: String): ConnectionIO[Option[RegistrationToken]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: RegistrationToken): F[RegistrationToken] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: RegistrationToken): ConnectionIO[RegistrationToken] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[RegistrationToken]): F[List[RegistrationToken]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[RegistrationToken]): ConnectionIO[List[RegistrationToken]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: String): F[RegistrationToken] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: String): ConnectionIO[RegistrationToken] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: RegistrationToken): F[RegistrationToken] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: RegistrationToken): ConnectionIO[RegistrationToken] = run(replaceAction(row))

  private def findAction(key: String) =
    quote {
      PublicSchema.RegistrationTokenDao.query.filter(a => a.email == lift(key))
    }

  private def insertAction(row: RegistrationToken): Quoted[ActionReturning[RegistrationToken, RegistrationToken]] =
    quote {
      PublicSchema.RegistrationTokenDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[RegistrationToken]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.RegistrationTokenDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: String) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: RegistrationToken) =
    quote {
      PublicSchema.RegistrationTokenDao.query
        .insert(lift(row))
        .onConflictUpdate(_.email)((t, e) => t -> e)
        .returning(x => x)
    }

}
