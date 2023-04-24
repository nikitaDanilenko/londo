package graphql.mutations.user.inputs

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class UpdateUserInput(
    displayName: Option[String],
    email: String
)

object UpdateUserInput {

  implicit val toInternal: Transformer[UpdateUserInput, services.user.Update] =
    Transformer
      .define[UpdateUserInput, services.user.Update]
      .buildTransformer

  implicit val decoder: Decoder[UpdateUserInput]                 = deriveDecoder[UpdateUserInput]
  implicit val inputObjectType: InputObjectType[UpdateUserInput] = deriveInputObjectType[UpdateUserInput]()
  implicit lazy val fromInput: FromInput[UpdateUserInput]        = circeDecoderFromInput[UpdateUserInput]

}
