package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class LogoutInput(
    logoutMode: LogoutMode
)

object LogoutInput {
  implicit val decoder: Decoder[LogoutInput]           = deriveDecoder[LogoutInput]
  implicit val inputType: InputObjectType[LogoutInput] = deriveInputObjectType[LogoutInput]()
  implicit lazy val fromInput: FromInput[LogoutInput]  = circeDecoderFromInput[LogoutInput]
}
