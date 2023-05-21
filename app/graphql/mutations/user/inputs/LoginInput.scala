package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class LoginInput(
    nickname: String,
    password: String,
    isValidityUnrestricted: Boolean
)

object LoginInput {
  implicit val inputObjectType: InputObjectType[LoginInput] = deriveInputObjectType[LoginInput]()
}
