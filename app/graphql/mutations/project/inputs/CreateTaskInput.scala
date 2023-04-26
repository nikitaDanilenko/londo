package graphql.mutations.project.inputs

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class CreateTaskInput(
    projectId: ProjectId,
    taskCreation: TaskCreation
)

object CreateTaskInput {
  implicit val inputType: InputObjectType[CreateTaskInput] = deriveInputObjectType[CreateTaskInput]()
  implicit lazy val fromInput: FromInput[CreateTaskInput]  = circeDecoderFromInput[CreateTaskInput]
}
