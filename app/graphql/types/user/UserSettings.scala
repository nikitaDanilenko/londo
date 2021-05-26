package graphql.types.user

import graphql.GraphQLContext
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class UserSettings(darkMode: Boolean)

object UserSettings {

  implicit val userSettingsType: ObjectType[GraphQLContext, UserSettings] =
    deriveObjectType[GraphQLContext, UserSettings]()

  def fromInternal(userSettings: services.user.UserSettings): UserSettings =
    UserSettings(
      darkMode = userSettings.darkMode
    )

  def toInternal(userSettings: UserSettings): services.user.UserSettings =
    services.user.UserSettings(
      darkMode = userSettings.darkMode
    )

}