package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateUserInput(
    displayName: Option[String]
)

object UpdateUserInput {

  implicit val toInternal: Transformer[UpdateUserInput, services.user.Update] =
    Transformer
      .define[UpdateUserInput, services.user.Update]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[UpdateUserInput] = deriveInputObjectType[UpdateUserInput]()

}
