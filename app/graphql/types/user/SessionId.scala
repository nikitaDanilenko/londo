package graphql.types.user

import graphql.types.util.NonEmptyList
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

// TODO: It's likely that this type is unnecessary
@JsonCodec
case class SessionId(uuid: UUID)

object SessionId {

  implicit val toInternal: Transformer[SessionId, db.SessionId] =
    _.uuid.transformInto[db.SessionId]

  implicit val fromInternal: Transformer[db.SessionId, SessionId] =
    SessionId(_)

  implicit val sessionIdObjectType: ObjectType[Unit, SessionId] = deriveObjectType[Unit, SessionId]()

  implicit val sessionIdInputObjectType: InputObjectType[SessionId] = deriveInputObjectType[SessionId](
    InputObjectTypeName("SessionIdInput")
  )

  implicit lazy val sessionIdFromInput: FromInput[SessionId] = circeDecoderFromInput[SessionId]

  implicit lazy val nonEmptyListOfSessionIdInputType: InputObjectType[NonEmptyList[SessionId]] =
    deriveInputObjectType[NonEmptyList[SessionId]](
      InputObjectTypeName("NonEmptyListOfSessionIdInput")
    )

  implicit lazy val nonEmptyListOfSessionIdFromInput: FromInput[NonEmptyList[SessionId]] =
    circeDecoderFromInput[NonEmptyList[SessionId]]

  implicit lazy val nonEmptyListOfSessionIdOutputType: ObjectType[Unit, NonEmptyList[SessionId]] =
    deriveObjectType[Unit, NonEmptyList[SessionId]]()

}
