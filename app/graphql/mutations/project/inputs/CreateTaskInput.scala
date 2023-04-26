package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateTaskInput(
    projectId: ProjectId,
    taskCreation: TaskCreation
)

object CreateTaskInput {
  implicit val inputObjectType: InputObjectType[CreateTaskInput] = deriveInputObjectType[CreateTaskInput]()
}
