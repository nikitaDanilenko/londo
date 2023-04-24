package graphql.mutations.user

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class UserUpdate(
    displayName: Option[String],
    email: String
)

object UserUpdate {

  implicit val toInternal: Transformer[UserUpdate, services.user.Update] =
    Transformer
      .define[UserUpdate, services.user.Update]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[UserUpdate] =
    deriveInputObjectType[UserUpdate]()

  implicit lazy val fromInput: FromInput[UserUpdate] = circeDecoderFromInput[UserUpdate]

}
