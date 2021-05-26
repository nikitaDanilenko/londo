package services.user

import cats.Order

import java.util.UUID

case class UserId(uuid: UUID) extends AnyVal

object UserId {

  def fromDb(userId: db.keys.UserId): UserId =
    UserId(
      uuid = userId.uuid
    )

  def toDb(userId: UserId): db.keys.UserId =
    db.keys.UserId(
      uuid = userId.uuid
    )

  implicit val orderUserId: Order[UserId] = Order.by(_.uuid)

}
