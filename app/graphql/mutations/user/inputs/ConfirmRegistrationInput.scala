package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class ConfirmRegistrationInput(
    creationToken: String,
    creationComplement: CreationComplement
)

object ConfirmRegistrationInput {
  implicit val decoder: Decoder[ConfirmRegistrationInput]           = deriveDecoder[ConfirmRegistrationInput]
  implicit val inputType: InputObjectType[ConfirmRegistrationInput] = deriveInputObjectType[ConfirmRegistrationInput]()
  implicit lazy val fromInput: FromInput[ConfirmRegistrationInput]  = circeDecoderFromInput[ConfirmRegistrationInput]
}
