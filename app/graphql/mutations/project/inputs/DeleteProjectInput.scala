package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class DeleteProjectInput(
    projectId: ProjectId
)

object DeleteProjectInput {

  implicit val decoder: Decoder[DeleteProjectInput] = deriveDecoder[DeleteProjectInput]

  implicit val inputObjectType: InputObjectType[DeleteProjectInput] =
    deriveInputObjectType[DeleteProjectInput]()

  implicit lazy val fromInput: FromInput[DeleteProjectInput] = circeDecoderFromInput[DeleteProjectInput]

}
