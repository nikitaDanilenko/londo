package services.user

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

}
