package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

case class AddTaskInput(
    projectId: ProjectId,
    taskCreation: TaskCreation
)

object AddTaskInput {
  implicit val decoder: Decoder[AddTaskInput]           = deriveDecoder[AddTaskInput]
  implicit val inputType: InputObjectType[AddTaskInput] = deriveInputObjectType[AddTaskInput]()
  implicit lazy val fromInput: FromInput[AddTaskInput]  = circeDecoderFromInput[AddTaskInput]
}
