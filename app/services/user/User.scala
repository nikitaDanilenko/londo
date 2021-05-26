package services.user

import db.keys
import db.keys.UserId

case class User(
    id: UserId,
    nickname: String,
    email: String,
    settings: UserSettings,
    details: UserDetails
)

object User {

  def fromRow(userRow: db.models.User, settings: UserSettings, details: UserDetails): User =
    User(
      id = keys.UserId(userRow.id),
      nickname = userRow.nickname,
      email = userRow.nickname,
      settings = settings,
      details = details
    )

  def toRow(user: User, passwordParameters: PasswordParameters): db.models.User =
    db.models.User(
      id = user.id.uuid,
      nickname = user.nickname,
      email = user.email,
      passwordSalt = passwordParameters.salt,
      passwordHash = passwordParameters.hash,
      iterations = passwordParameters.iterations.intValue
    )

}
