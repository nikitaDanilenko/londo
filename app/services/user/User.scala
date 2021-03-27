package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class User(
    id: UserId,
    nickname: String,
    email: String,
    passwordSalt: String,
    passwordHash: String,
    settings: UserSettings,
    details: UserDetails
)

object User {

  def fromRow(userRow: db.models.User, settings: UserSettings, details: UserDetails): User =
    User(
      id = UserId(userRow.id),
      nickname = userRow.nickname,
      email = userRow.nickname,
      passwordSalt = userRow.passwordSalt,
      passwordHash = userRow.passwordHash,
      settings = settings,
      details = details
    )

  def toRow(user: User): db.models.User =
    db.models.User(
      id = user.id.uuid,
      nickname = user.nickname,
      email = user.email,
      passwordSalt = user.passwordSalt,
      passwordHash = user.passwordHash
    )

}
