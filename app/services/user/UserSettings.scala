package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UserSettings(darkMode: Boolean)

object UserSettings {

  val default: UserSettings = UserSettings(
    darkMode = false
  )

  def fromRow(userSettingsRow: db.models.UserSettings): UserSettings =
    UserSettings(
      darkMode = userSettingsRow.darkMode
    )

}
