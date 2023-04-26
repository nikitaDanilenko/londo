package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteTaskInput(
    projectId: ProjectId,
    taskId: TaskId
)

object DeleteTaskInput {
  implicit val inputType: InputObjectType[DeleteTaskInput] = deriveInputObjectType[DeleteTaskInput]()
  implicit lazy val fromInput: FromInput[DeleteTaskInput]  = circeDecoderFromInput[DeleteTaskInput]
}
