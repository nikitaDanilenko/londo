package graphql.types.user

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class User(
    id: UserId,
    nickname: String,
    email: String,
    settings: UserSettings,
    details: UserDetails
)

object User {

  implicit val userObjectType: ObjectType[Unit, User] = deriveObjectType[Unit, User]()

  def fromInternal(user: services.user.User): User =
    User(
      id = UserId.fromInternal(user.id),
      nickname = user.nickname,
      email = user.email,
      settings = UserSettings.fromInternal(user.settings),
      details = UserDetails.fromInternal(user.details)
    )

}
