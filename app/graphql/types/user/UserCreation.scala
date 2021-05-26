package graphql.types.user

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class UserCreation(
    nickname: String,
    email: String,
    password: String,
    token: String
)

object UserCreation {

  def toInternal(userCreation: UserCreation): services.user.UserCreation =
    services.user.UserCreation(
      nickname = userCreation.nickname,
      email = userCreation.email,
      password = userCreation.password,
      token = userCreation.token
    )

  implicit val userCreationInputType: InputObjectType[UserCreation] =
    deriveInputObjectType[UserCreation]()

  implicit val userCreationFromInput: FromInput[UserCreation] = circeDecoderFromInput[UserCreation]
}
