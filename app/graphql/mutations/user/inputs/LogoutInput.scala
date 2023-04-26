package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class LogoutInput(
    logoutMode: LogoutMode
)

object LogoutInput {
  implicit val inputType: InputObjectType[LogoutInput] = deriveInputObjectType[LogoutInput]()
  implicit lazy val fromInput: FromInput[LogoutInput]  = circeDecoderFromInput[LogoutInput]
}
