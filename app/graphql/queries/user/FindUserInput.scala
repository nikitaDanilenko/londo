package graphql.queries.user

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FindUserInput(
    searchString: String
)

object FindUserInput {

  implicit val inputObjectType: InputObjectType[FindUserInput] = deriveInputObjectType[FindUserInput]()
  implicit lazy val fromInput: FromInput[FindUserInput]        = circeDecoderFromInput[FindUserInput]
}
