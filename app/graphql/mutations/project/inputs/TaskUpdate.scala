package graphql.mutations.project.inputs

import graphql.types.task.TaskKind
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class TaskUpdate(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    counting: Boolean,
    progressUpdate: ProgressUpdate
)

object TaskUpdate {

  implicit val toInternal: Transformer[TaskUpdate, services.task.Update] =
    Transformer
      .define[TaskUpdate, services.task.Update]
      .buildTransformer

  implicit val taskUpdatePlainUpdateInputObjectType: InputObjectType[TaskUpdate] =
    deriveInputObjectType[TaskUpdate]()

  implicit lazy val taskUpdatePlainUpdateFromInput: FromInput[TaskUpdate] = circeDecoderFromInput[TaskUpdate]

}
