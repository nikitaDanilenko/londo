package graphql.types.user

import graphql.types.FromAndToInternal
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

  implicit val userIdFromAndToInternal: FromAndToInternal[UserId, services.user.UserId] = FromAndToInternal.create(
    fromInternal = userId =>
      UserId(
        uuid = userId.uuid
      ),
    toInternal = userId =>
      services.user.UserId(
        uuid = userId.uuid
      )
  )

  implicit val userIdObjectType: ObjectType[Unit, UserId] = deriveObjectType[Unit, UserId]()

  implicit val userIdInputObjectType: InputObjectType[UserId] = deriveInputObjectType[UserId](
    InputObjectTypeName("UserIdInput")
  )

  implicit lazy val userIdFromInput: FromInput[UserId] = circeDecoderFromInput[UserId]

}
