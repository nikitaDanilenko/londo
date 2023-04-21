package graphql.types.user

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class UserCreation(
    nickname: String,
    password: String,
    displayName: Option[String],
    email: String
)

object UserCreation {

  implicit val toInternal: Transformer[UserCreation, services.user.UserCreation] =
    Transformer
      .define[UserCreation, services.user.UserCreation]
      .buildTransformer

  implicit val userCreationInputType: InputObjectType[UserCreation] =
    deriveInputObjectType[UserCreation]()

  implicit lazy val userCreationFromInput: FromInput[UserCreation] = circeDecoderFromInput[UserCreation]
}
