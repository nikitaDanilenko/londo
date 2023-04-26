package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdatePasswordInput(
    password: String
)

object UpdatePasswordInput {
  implicit val inputType: InputObjectType[UpdatePasswordInput] = deriveInputObjectType[UpdatePasswordInput]()
  implicit lazy val fromInput: FromInput[UpdatePasswordInput]  = circeDecoderFromInput[UpdatePasswordInput]
}
