package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class UpdatePasswordInput(
    password: String
)

object UpdatePasswordInput {
  implicit val decoder: Decoder[UpdatePasswordInput]           = deriveDecoder[UpdatePasswordInput]
  implicit val inputType: InputObjectType[UpdatePasswordInput] = deriveInputObjectType[UpdatePasswordInput]()
  implicit lazy val fromInput: FromInput[UpdatePasswordInput]  = circeDecoderFromInput[UpdatePasswordInput]
}
