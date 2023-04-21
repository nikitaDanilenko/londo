package graphql.types.task

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class TaskId(uuid: UUID)

object TaskId {

  implicit val toInternal: Transformer[TaskId, db.TaskId] =
    _.uuid.transformInto[db.TaskId]

  implicit val fromInternal: Transformer[db.TaskId, TaskId] =
    TaskId(_)

  implicit val objectType: ObjectType[Unit, TaskId] = deriveObjectType[Unit, TaskId]()

  implicit val inputObjectType: InputObjectType[TaskId] = deriveInputObjectType[TaskId](
    InputObjectTypeName("PlainTaskIdInput")
  )

  implicit lazy val fromInput: FromInput[TaskId] = circeDecoderFromInput[TaskId]

}
