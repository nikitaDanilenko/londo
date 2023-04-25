package graphql.queries.project

import graphql.types.project.ProjectId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class FetchResolvedProjectInput(
    projectId: ProjectId
)

object FetchResolvedProjectInput {
  implicit val decoder: Decoder[FetchResolvedProjectInput] = deriveDecoder[FetchResolvedProjectInput]

  implicit val inputType: InputObjectType[FetchResolvedProjectInput] =
    deriveInputObjectType[FetchResolvedProjectInput]()

  implicit lazy val fromInput: FromInput[FetchResolvedProjectInput] = circeDecoderFromInput[FetchResolvedProjectInput]
}