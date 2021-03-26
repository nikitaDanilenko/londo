package db.generated.daos

import cats.effect.{ Async, ContextShift }
import db.DbContext
import db.models._
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class UserDetailsDAO @Inject() (dbContext: DbContext) {
  import dbContext._

  def find[F[_]: Async: ContextShift](key: UUID): F[Option[UserDetails]] =
    run(findAction(key)).map(_.headOption).transact(transactor[F])

  def insert[F[_]: Async: ContextShift](row: UserDetails): F[UserDetails] =
    run(insertAction(row)).transact(transactor[F])

  def insertAll[F[_]: Async: ContextShift](rows: Seq[UserDetails]): F[List[UserDetails]] =
    run(insertAllAction(rows)).transact(transactor[F])

  def delete[F[_]: Async: ContextShift](key: UUID): F[UserDetails] = run(deleteAction(key)).transact(transactor[F])

  def update[F[_]: Async: ContextShift](row: UserDetails): F[UserDetails] =
    run(updateAction(row)).transact(transactor[F])

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

  private def updateAction(row: UserDetails) =
    quote {
      PublicSchema.UserDetailsDao.query.update(lift(row)).returning(x => x)
    }

}
