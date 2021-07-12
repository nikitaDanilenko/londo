package graphql.types.user

import graphql.types.FromInternal
import graphql.types.FromInternal.syntax._
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

  implicit val userFromInternal: FromInternal[User, services.user.User] = user =>
    User(
      id = user.id.fromInternal,
      nickname = user.nickname,
      email = user.email,
      settings = user.settings.fromInternal,
      details = user.details.fromInternal
    )

  implicit val userObjectType: ObjectType[Unit, User] = deriveObjectType[Unit, User]()

}
