package graphql.types.project

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class ProjectUpdate(
    name: String,
    description: Option[String]
)

object ProjectUpdate {

  implicit val toInternal: Transformer[ProjectUpdate, services.project.Update] =
    Transformer
      .define[ProjectUpdate, services.project.Update]
      .buildTransformer

  implicit val projectUpdateInputObjectType: InputObjectType[ProjectUpdate] = deriveInputObjectType[ProjectUpdate]()

  implicit lazy val projectUpdateFromInput: FromInput[ProjectUpdate] = circeDecoderFromInput[ProjectUpdate]

}
