package graphql.types.user

import graphql.types.FromAndToInternal
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class UserSettings(darkMode: Boolean)

object UserSettings {

  implicit lazy val userSettingsFromAndToInternal: FromAndToInternal[UserSettings, services.user.UserSettings] =
    FromAndToInternal.create(
      fromInternal = userSettings =>
        UserSettings(
          darkMode = userSettings.darkMode
        ),
      toInternal = userSettings =>
        services.user.UserSettings(
          darkMode = userSettings.darkMode
        )
    )

  implicit val userSettingsObjectType: ObjectType[Unit, UserSettings] = deriveObjectType[Unit, UserSettings]()

}
