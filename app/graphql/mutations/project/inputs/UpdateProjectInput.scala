package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateProjectInput(
    projectId: ProjectId,
    name: String,
    description: Option[String]
)

object UpdateProjectInput {

  implicit val toInternal: Transformer[UpdateProjectInput, services.project.Update] =
    Transformer
      .define[UpdateProjectInput, services.project.Update]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[UpdateProjectInput] = deriveInputObjectType[UpdateProjectInput]()
  implicit lazy val fromInput: FromInput[UpdateProjectInput]        = circeDecoderFromInput[UpdateProjectInput]

}
