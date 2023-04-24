package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class RequestRegistrationInput(
    userIdentifier: UserIdentifier
)

object RequestRegistrationInput {
  implicit val decoder: Decoder[RequestRegistrationInput]           = deriveDecoder[RequestRegistrationInput]
  implicit val inputType: InputObjectType[RequestRegistrationInput] = deriveInputObjectType[RequestRegistrationInput]()
  implicit lazy val fromInput: FromInput[RequestRegistrationInput]  = circeDecoderFromInput[RequestRegistrationInput]
}
