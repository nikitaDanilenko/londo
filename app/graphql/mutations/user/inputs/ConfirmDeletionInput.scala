package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class ConfirmDeletionInput(
    deletionToken: String
)

object ConfirmDeletionInput {
  implicit val inputType: InputObjectType[ConfirmDeletionInput] = deriveInputObjectType[ConfirmDeletionInput]()
  implicit lazy val fromInput: FromInput[ConfirmDeletionInput]  = circeDecoderFromInput[ConfirmDeletionInput]
}
