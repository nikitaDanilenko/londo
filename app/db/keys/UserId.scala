package db.keys

import cats.Order
import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class UserId(uuid: UUID) extends AnyVal

object UserId {
  implicit val orderUserId: Order[UserId] = Order.by(_.uuid)
}
