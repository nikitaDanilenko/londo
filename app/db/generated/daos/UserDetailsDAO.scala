package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.models._
import db.{ DbContext, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserDetailsDAO @Inject() (dbContext: DbContext, dbTransactorProvider: DbTransactorProvider) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[UserDetails]] =
    findF(key).transact(dbTransactorProvider.transactor[F])

  def findF(key: UUID): ConnectionIO[Option[UserDetails]] = run(findAction(key)).map(_.headOption)

  def insert[F[_]: Async: ContextShift](row: UserDetails): F[UserDetails] =
    insertF(row).transact(dbTransactorProvider.transactor[F])

  def insertF(row: UserDetails): ConnectionIO[UserDetails] = run(insertAction(row))

  def insertAll[F[_]: Async: ContextShift](rows: Seq[UserDetails]): F[List[UserDetails]] =
    insertAllF(rows).transact(dbTransactorProvider.transactor[F])

  def insertAllF(rows: Seq[UserDetails]): ConnectionIO[List[UserDetails]] = run(insertAllAction(rows))

  def delete[F[_]: Async: ContextShift](key: UUID): F[UserDetails] =
    deleteF(key).transact(dbTransactorProvider.transactor[F])

  def deleteF(key: UUID): ConnectionIO[UserDetails] = run(deleteAction(key))

  def replace[F[_]: Async: ContextShift](row: UserDetails): F[UserDetails] =
    run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])

  def replaceF(row: UserDetails): ConnectionIO[UserDetails] = run(replaceAction(row))

  private def findAction(key: UUID) =
    quote {
      PublicSchema.UserDetailsDao.query.filter(a => a.userId == lift(key))
    }

  private def insertAction(row: UserDetails): Quoted[ActionReturning[UserDetails, UserDetails]] =
    quote {
      PublicSchema.UserDetailsDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[UserDetails]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.UserDetailsDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: UUID) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: UserDetails) =
    quote {
      PublicSchema.UserDetailsDao.query.insert(lift(row)).onConflictUpdate(_.userId)((t, e) => t -> e).returning(x => x)
    }

}
