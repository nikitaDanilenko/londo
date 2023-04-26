package graphql.queries.project

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class FetchProjectInput(
    projectId: ProjectId
)

object FetchProjectInput {
  implicit val inputType: InputObjectType[FetchProjectInput] = deriveInputObjectType[FetchProjectInput]()
  implicit lazy val fromInput: FromInput[FetchProjectInput]  = circeDecoderFromInput[FetchProjectInput]
}
