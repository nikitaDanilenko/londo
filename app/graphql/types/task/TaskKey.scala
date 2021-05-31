package graphql.types.task

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

@JsonCodec
case class TaskKey(
    projectId: ProjectId,
    taskId: TaskId
)

object TaskKey {

  def toInternal(taskKey: TaskKey): services.task.TaskKey =
    services.task.TaskKey(
      projectId = ProjectId.toInternal(taskKey.projectId),
      taskId = TaskId.toInternal(taskKey.taskId)
    )

  implicit val taskKeyInputType: InputObjectType[TaskKey] =
    deriveInputObjectType[TaskKey]()

  implicit val taskKeyObjectType: ObjectType[Unit, TaskKey] =
    deriveObjectType[Unit, TaskKey]()

  implicit lazy val taskKeyFromInput: FromInput[TaskKey] = circeDecoderFromInput[TaskKey]

}
