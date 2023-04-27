package graphql.queries.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FindUserInput(
    searchString: String
)

object FindUserInput {
  implicit val inputObjectType: InputObjectType[FindUserInput] = deriveInputObjectType[FindUserInput]()
}
