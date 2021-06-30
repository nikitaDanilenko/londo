package graphql.types.task

import graphql.types.FromAndToInternal
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class TaskId(uuid: UUID)

object TaskId {

  implicit val taskIdFromAndToInternal: FromAndToInternal[TaskId, services.task.TaskId] = FromAndToInternal.create(
    fromInternal = taskId =>
      TaskId(
        uuid = taskId.uuid
      ),
    toInternal = taskId =>
      services.task.TaskId(
        uuid = taskId.uuid
      )
  )

  implicit val taskIdObjectType: ObjectType[Unit, TaskId] = deriveObjectType[Unit, TaskId]()

  implicit val taskIdInputObjectType: InputObjectType[TaskId] = deriveInputObjectType[TaskId](
    InputObjectTypeName("TaskIdInput")
  )

  implicit lazy val taskIdFromInput: FromInput[TaskId] = circeDecoderFromInput[TaskId]

}
