package graphql.queries.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchResolvedProjectInput(
    projectId: ProjectId
)

object FetchResolvedProjectInput {

  implicit val inputObjectType: InputObjectType[FetchResolvedProjectInput] =
    deriveInputObjectType[FetchResolvedProjectInput]()

}
