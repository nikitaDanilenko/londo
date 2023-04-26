package graphql.mutations.project.inputs

import graphql.types.task.TaskKind
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
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

  implicit val inputObjectType: InputObjectType[TaskUpdate] =
    deriveInputObjectType[TaskUpdate]()

}
