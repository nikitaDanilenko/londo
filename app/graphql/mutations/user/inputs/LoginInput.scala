package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class LoginInput(
    nickname: String,
    password: String,
    isValidityUnrestricted: Boolean
)

object LoginInput {

  implicit val decoder: Decoder[LoginInput]                 = deriveDecoder[LoginInput]
  implicit val inputObjectType: InputObjectType[LoginInput] = deriveInputObjectType[LoginInput]()
  implicit lazy val fromInput: FromInput[LoginInput]        = circeDecoderFromInput[LoginInput]

}
