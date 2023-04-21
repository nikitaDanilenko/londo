package graphql.types.project

import graphql.types.project
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class ProjectCreation(
    name: String,
    description: Option[String]
)

object ProjectCreation {

  implicit val toInternal: Transformer[project.ProjectCreation, services.project.Creation] =
    Transformer
      .define[project.ProjectCreation, services.project.Creation]
      .buildTransformer

  implicit val projectCreationInputObjectType: InputObjectType[ProjectCreation] =
    deriveInputObjectType[ProjectCreation]()

  implicit lazy val projectCreationFromInput: FromInput[ProjectCreation] = circeDecoderFromInput[ProjectCreation]

}
