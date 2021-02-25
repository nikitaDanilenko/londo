package services.user

object UserConverter {

  def fromRow(userRow: db.models.User): User =
    User(
      id = UserId(userRow.id),
      nickname = userRow.nickname,
      email = userRow.email,
      passwordSalt = userRow.passwordSalt,
      passwordHash = userRow.passwordHash
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
