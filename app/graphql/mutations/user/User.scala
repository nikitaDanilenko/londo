package graphql.mutations.user

import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class User(
    id: UserId,
    nickname: String,
    displayName: Option[String],
    email: String
)

object User {

  implicit val fromInternal: Transformer[services.user.User, User] = user =>
    User(
      id = user.id.transformInto[UserId],
      nickname = user.nickname,
      displayName = user.displayName,
      email = user.email
    )

  implicit val userObjectType: ObjectType[Unit, User] = deriveObjectType[Unit, User]()

}
