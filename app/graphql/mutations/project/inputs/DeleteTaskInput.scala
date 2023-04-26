package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import graphql.types.task.TaskId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DeleteTaskInput(
    projectId: ProjectId,
    taskId: TaskId
)

object DeleteTaskInput {
  implicit val inputObjectType: InputObjectType[DeleteTaskInput] = deriveInputObjectType[DeleteTaskInput]()
}
