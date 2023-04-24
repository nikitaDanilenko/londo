package graphql.mutations.project.inputs

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class CreateProjectInput(
    name: String,
    description: Option[String]
)

object CreateProjectInput {

  implicit val toInternal: Transformer[CreateProjectInput, services.project.Creation] =
    Transformer
      .define[CreateProjectInput, services.project.Creation]
      .buildTransformer

  implicit val projectCreationInputObjectType: InputObjectType[CreateProjectInput] =
    deriveInputObjectType[CreateProjectInput]()

  implicit lazy val projectCreationFromInput: FromInput[CreateProjectInput] = circeDecoderFromInput[CreateProjectInput]

}
