package graphql.types.user

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType
import services.user.User

@JsonCodec
case class UserIdentifier(
    nickname: String,
    email: String
)

object UserIdentifier {

  def of(user: User): UserIdentifier =
    UserIdentifier(
      nickname = user.nickname,
      email = user.email
    )

  implicit val inputType: InputObjectType[UserIdentifier] =
    deriveInputObjectType[UserIdentifier]()

  implicit lazy val fromInput: FromInput[UserIdentifier] = circeDecoderFromInput[UserIdentifier]
}
