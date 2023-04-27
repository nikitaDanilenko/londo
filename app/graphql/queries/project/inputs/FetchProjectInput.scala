package graphql.queries.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchProjectInput(
    projectId: ProjectId
)

object FetchProjectInput {
  implicit val inputObjectType: InputObjectType[FetchProjectInput] = deriveInputObjectType[FetchProjectInput]()
}
