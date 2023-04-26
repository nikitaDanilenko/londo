package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class UpdateTaskInput(
    projectId: ProjectId,
    taskId: TaskId,
    taskUpdate: TaskUpdate
)

object UpdateTaskInput {
  implicit val inputType: InputObjectType[UpdateTaskInput] = deriveInputObjectType[UpdateTaskInput]()
  implicit lazy val fromInput: FromInput[UpdateTaskInput]  = circeDecoderFromInput[UpdateTaskInput]
}
