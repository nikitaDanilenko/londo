package services.task

import graphql.GraphQLContext
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

import java.util.UUID

@JsonCodec
case class TaskId(uuid: UUID) extends AnyVal

object TaskId {
  implicit val taskIdObjectType: ObjectType[GraphQLContext, TaskId] = deriveObjectType()
}
