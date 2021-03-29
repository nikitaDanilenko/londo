package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class SessionKeyDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[SessionKey]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[SessionKey]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: SessionKey): F[SessionKey] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: SessionKey): ConnectionIO[SessionKey] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[SessionKey]): F[List[SessionKey]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[SessionKey]): ConnectionIO[List[SessionKey]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[SessionKey] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[SessionKey] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: SessionKey): F[SessionKey] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: SessionKey): ConnectionIO[SessionKey] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.SessionKeyDao.query.filter(a => a.userId == lift(key))
    }

  private def insertAction(row: SessionKey): Quoted[ActionReturning[SessionKey, SessionKey]] =
    quote {
      PublicSchema.SessionKeyDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[SessionKey]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.SessionKeyDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
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
