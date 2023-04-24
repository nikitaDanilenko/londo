package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class CreationComplement(
    displayName: Option[String],
    password: String
)

object CreationComplement {

  implicit val inputObjectType: InputObjectType[CreationComplement] =
    deriveInputObjectType[CreationComplement]()

  implicit lazy val fromInput: FromInput[CreationComplement] = circeDecoderFromInput[CreationComplement]

}
