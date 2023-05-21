package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class ConfirmRecoveryInput(
    recoveryToken: String,
    password: String
)

object ConfirmRecoveryInput {
  implicit val inputType: InputObjectType[ConfirmRecoveryInput] = deriveInputObjectType[ConfirmRecoveryInput]()
}
