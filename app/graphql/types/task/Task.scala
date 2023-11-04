package graphql.types.task

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class Task(
    id: TaskId,
    name: String,
    taskKind: TaskKind,
    unit: Option[String],
    progress: Progress,
    counting: Boolean
)

object Task {

  implicit val fromInternal: Transformer[services.task.Task, Task] =
    Transformer
      .define[services.task.Task, Task]
      .buildTransformer

  implicit val toProcessing: Transformer[Task, processing.statistics.dashboard.Task] =
    Transformer
      .define[Task, processing.statistics.dashboard.Task]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, Task] = deriveObjectType[Unit, Task]()

}
