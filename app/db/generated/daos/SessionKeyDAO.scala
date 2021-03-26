package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class SessionKeyDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[SessionKey]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: SessionKey): F[SessionKey] = run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[SessionKey]): F[List[SessionKey]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[SessionKey] = run(deleteAction(key)).transact(transactor[F])
  def update[F[_]: Async: ContextShift](row: SessionKey): F[SessionKey] = run(updateAction(row)).transact(transactor[F])

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

  private def updateAction(row: SessionKey) =
    quote {
      PublicSchema.SessionKeyDao.query.update(lift(row)).returning(x => x)
    }

}