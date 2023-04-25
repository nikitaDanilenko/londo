package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class CreateTaskInput(
    projectId: ProjectId,
    taskCreation: TaskCreation
)

object CreateTaskInput {
  implicit val decoder: Decoder[CreateTaskInput]           = deriveDecoder[CreateTaskInput]
  implicit val inputType: InputObjectType[CreateTaskInput] = deriveInputObjectType[CreateTaskInput]()
  implicit lazy val fromInput: FromInput[CreateTaskInput]  = circeDecoderFromInput[CreateTaskInput]
}
