package db.daos.loginThrottle

import db.generated.Tables
import db.{ DAOActions, UserId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.LoginThrottleRow, UserId] {

  override val keyOf: Tables.LoginThrottleRow => UserId = _.userId.transformInto[UserId]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.LoginThrottleRow, Tables.LoginThrottle, UserId](
      Tables.LoginThrottle,
      (table, key) => table.userId === key.transformInto[UUID]
    ) with DAO

}
