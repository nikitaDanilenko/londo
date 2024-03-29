package graphql.types.user

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }
import utils.transformer.implicits._
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class UserId(uuid: UUID)

object UserId {

  implicit val toInternal: Transformer[UserId, db.UserId] =
    _.uuid.transformInto[db.UserId]

  implicit val fromInternal: Transformer[db.UserId, UserId] =
    UserId(_)

  implicit val objectType: ObjectType[Unit, UserId] = deriveObjectType[Unit, UserId]()

  implicit val inputObjectType: InputObjectType[UserId] = deriveInputObjectType[UserId](
    InputObjectTypeName("UserIdInput")
  )

}
