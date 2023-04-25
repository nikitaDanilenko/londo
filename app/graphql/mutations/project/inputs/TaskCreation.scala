package graphql.mutations.project.inputs

import graphql.types.task.{ Progress, TaskKind }
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class TaskCreation(
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean
)

object TaskCreation {

  implicit val toInternal: Transformer[TaskCreation, services.task.Creation] =
    Transformer
      .define[TaskCreation, services.task.Creation]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[TaskCreation] =
    deriveInputObjectType[TaskCreation]()

  implicit lazy val fromInput: FromInput[TaskCreation] =
    circeDecoderFromInput[TaskCreation]

}