package graphql.types.task

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive
import sangria.macros.derive.deriveObjectType
import sangria.schema.{ObjectType, OutputType}
import services.task.Task

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

  implicit val fromInternal: Transformer[Task, Task] =
    Transformer
      .define[Task, Task]
      .buildTransformer

  implicit val plainObjectType: ObjectType[Unit, Task] = deriveObjectType[Unit, Task]()

}
