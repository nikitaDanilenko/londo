package graphql.types.task

import io.circe.generic.JsonCodec
import sangria.macros.derive.{ deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class TaskId(uuid: UUID)

object TaskId {

  def fromInternal(taskId: services.task.TaskId): TaskId =
    TaskId(
      uuid = taskId.uuid
    )

  def toInternal(taskId: TaskId): services.task.TaskId =
    services.task.TaskId(
      uuid = taskId.uuid
    )

  implicit val taskIdObjectType: ObjectType[Unit, TaskId] = deriveObjectType[Unit, TaskId]()
  implicit val taskIdInputObjectType: InputObjectType[TaskId] = deriveInputObjectType[TaskId]()
  implicit lazy val taskIdFromInput: FromInput[TaskId] = circeDecoderFromInput[TaskId]

}
