package graphql.types.user

import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class UserId(uuid: UUID)

object UserId {

  implicit val userIdObjectType: ObjectType[Unit, UserId] = deriveObjectType[Unit, UserId]()

  implicit val userIdInputObjectType: InputObjectType[UserId] = deriveInputObjectType[UserId](
    InputObjectTypeName("UserIdInput")
  )

  implicit lazy val userIdFromInput: FromInput[UserId] = circeDecoderFromInput[UserId]

  def fromInternal(userId: services.user.UserId): UserId =
    UserId(
      uuid = userId.uuid
    )

  def toInternal(userId: UserId): services.user.UserId =
    services.user.UserId(
      uuid = userId.uuid
    )

}
