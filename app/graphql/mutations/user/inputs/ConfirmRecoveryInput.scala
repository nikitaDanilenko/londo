package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class ConfirmRecoveryInput(
    recoveryToken: String,
    password: String
)

object ConfirmRecoveryInput {
  implicit val decoder: Decoder[ConfirmRecoveryInput]           = deriveDecoder[ConfirmRecoveryInput]
  implicit val inputType: InputObjectType[ConfirmRecoveryInput] = deriveInputObjectType[ConfirmRecoveryInput]()
  implicit lazy val fromInput: FromInput[ConfirmRecoveryInput]  = circeDecoderFromInput[ConfirmRecoveryInput]
}
