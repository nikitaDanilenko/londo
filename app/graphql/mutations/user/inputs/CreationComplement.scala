package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreationComplement(
    displayName: Option[String],
    password: String
)

object CreationComplement {

  implicit val inputObjectType: InputObjectType[CreationComplement] =
    deriveInputObjectType[CreationComplement]()

}
