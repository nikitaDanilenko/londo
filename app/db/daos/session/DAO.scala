package db.daos.session

import db.generated.Tables
import db.{ DAOActions, UserId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.sql.Timestamp
import java.util.UUID

trait DAO extends DAOActions[Tables.SessionRow, SessionKey] {

  override val keyOf: Tables.SessionRow => SessionKey = SessionKey.of

  def deleteAllFor(userId: UserId): DBIO[Int]

  def deleteAllBefore(userId: UserId, timestamp: Timestamp): DBIO[Int]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.SessionRow, Tables.Session, SessionKey](
      Tables.Session,
      (table, key) => table.userId === key.userId.transformInto[UUID] && table.id === key.sessionId.transformInto[UUID]
    ) with DAO {

      override def deleteAllFor(userId: UserId): DBIO[Int] =
        Tables.Session
          .filter(_.userId === userId.transformInto[UUID])
          .delete

      override def deleteAllBefore(userId: UserId, timestamp: Timestamp): DBIO[Int] =
        Tables.Session
          .filter(session => session.userId === userId.transformInto[UUID] && session.createdAt < timestamp)
          .delete

    }

}
