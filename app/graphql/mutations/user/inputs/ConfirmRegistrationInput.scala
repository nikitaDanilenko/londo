package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class ConfirmRegistrationInput(
    creationToken: String,
    creationComplement: CreationComplement
)

object ConfirmRegistrationInput {
  implicit val inputType: InputObjectType[ConfirmRegistrationInput] = deriveInputObjectType[ConfirmRegistrationInput]()
}
