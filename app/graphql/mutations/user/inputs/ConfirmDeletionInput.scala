package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class ConfirmDeletionInput(
    deletionToken: String
)

object ConfirmDeletionInput {
  implicit val decoder: Decoder[ConfirmDeletionInput]           = deriveDecoder[ConfirmDeletionInput]
  implicit val inputType: InputObjectType[ConfirmDeletionInput] = deriveInputObjectType[ConfirmDeletionInput]()
  implicit lazy val fromInput: FromInput[ConfirmDeletionInput]  = circeDecoderFromInput[ConfirmDeletionInput]
}
