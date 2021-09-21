package services.user

import cats.Order

import java.util.UUID

case class SessionId(uuid: UUID) extends AnyVal

object SessionId {

  def fromDb(sessionId: db.keys.SessionId): SessionId =
    SessionId(
      uuid = sessionId.uuid
    )

  def toDb(sessionId: SessionId): db.keys.SessionId =
    db.keys.SessionId(
      uuid = sessionId.uuid
    )

  implicit val orderSessionId: Order[SessionId] = Order.by(_.uuid)
}
