package graphql.mutations.user.inputs

import graphql.types.user.UserId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class RequestRecoveryInput(
    userId: UserId
)

object RequestRecoveryInput {
  implicit val decoder: Decoder[RequestRecoveryInput]           = deriveDecoder[RequestRecoveryInput]
  implicit val inputType: InputObjectType[RequestRecoveryInput] = deriveInputObjectType[RequestRecoveryInput]()
  implicit lazy val fromInput: FromInput[RequestRecoveryInput]  = circeDecoderFromInput[RequestRecoveryInput]
}
