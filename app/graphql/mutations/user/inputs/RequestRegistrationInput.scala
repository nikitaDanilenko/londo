package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class RequestRegistrationInput(
    userIdentifier: UserIdentifier
)

object RequestRegistrationInput {
  implicit val inputType: InputObjectType[RequestRegistrationInput] = deriveInputObjectType[RequestRegistrationInput]()
}
