package db.daos.loginAttempt

import db.generated.Tables
import db.{ DAOActions, UserId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.LoginAttemptRow, UserId] {

  override val keyOf: Tables.LoginAttemptRow => UserId = _.userId.transformInto[UserId]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.LoginAttemptRow, Tables.LoginAttempt, UserId](
      Tables.LoginAttempt,
      (table, key) => table.userId === key.transformInto[UUID]
    ) with DAO

}
