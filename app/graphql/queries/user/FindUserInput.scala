package graphql.queries.user

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class FindUserInput(
    searchString: String
)

object FindUserInput {

  implicit val decoder: Decoder[FindUserInput] = deriveDecoder[FindUserInput]

  implicit val userUpdateInputType: InputObjectType[FindUserInput] =
    deriveInputObjectType[FindUserInput]()

  implicit lazy val userUpdateFromInput: FromInput[FindUserInput] = circeDecoderFromInput[FindUserInput]
}
