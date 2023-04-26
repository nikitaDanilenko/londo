package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class RequestRegistrationInput(
    userIdentifier: UserIdentifier
)

object RequestRegistrationInput {
  implicit val inputType: InputObjectType[RequestRegistrationInput] = deriveInputObjectType[RequestRegistrationInput]()
  implicit lazy val fromInput: FromInput[RequestRegistrationInput]  = circeDecoderFromInput[RequestRegistrationInput]
}
