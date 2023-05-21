package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class LogoutInput(
    logoutMode: LogoutMode
)

object LogoutInput {
  implicit val inputType: InputObjectType[LogoutInput] = deriveInputObjectType[LogoutInput]()
}
