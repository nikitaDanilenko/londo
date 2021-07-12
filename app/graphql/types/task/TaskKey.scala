package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

@JsonCodec
case class TaskKey(
    projectId: ProjectId,
    taskId: TaskId
)

object TaskKey {

  implicit val taskKeyToInternal: ToInternal[TaskKey, services.task.TaskKey] = taskKey =>
    services.task.TaskKey(
      projectId = taskKey.projectId.toInternal,
      taskId = taskKey.taskId.toInternal
    )

  implicit val taskKeyInputType: InputObjectType[TaskKey] =
    deriveInputObjectType[TaskKey](
      InputObjectTypeName("TaskKeyInput")
    )

  implicit val taskKeyObjectType: ObjectType[Unit, TaskKey] =
    deriveObjectType[Unit, TaskKey]()

  implicit lazy val taskKeyFromInput: FromInput[TaskKey] = circeDecoderFromInput[TaskKey]

}
