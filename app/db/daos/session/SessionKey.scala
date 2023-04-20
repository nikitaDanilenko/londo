package db.daos.session

import db.generated.Tables
import db.{ SessionId, UserId }
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

case class SessionKey(
    userId: UserId,
    sessionId: SessionId
)

object SessionKey {

  def of(row: Tables.SessionRow): SessionKey =
    SessionKey(
      row.userId.transformInto[UserId],
      row.id.transformInto[SessionId]
    )

}
