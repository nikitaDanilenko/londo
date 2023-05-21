package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteProjectInput(
    projectId: ProjectId
)

object DeleteProjectInput {

  implicit val inputObjectType: InputObjectType[DeleteProjectInput] = deriveInputObjectType[DeleteProjectInput]()

}
