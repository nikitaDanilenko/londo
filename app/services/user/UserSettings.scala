package services.user

case class UserSettings(darkMode: Boolean)

object UserSettings {

  val default: UserSettings = UserSettings(
    darkMode = false
  )

  def fromRow(userSettingsRow: db.models.UserSettings): UserSettings =
    UserSettings(
      darkMode = userSettingsRow.darkMode
    )

  def toRow(userId: UserId, userSettings: UserSettings): db.models.UserSettings =
    db.models.UserSettings(
      userId = userId.uuid,
      darkMode = userSettings.darkMode
    )

}
