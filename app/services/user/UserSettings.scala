package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UserSettings(darkMode: Boolean)
