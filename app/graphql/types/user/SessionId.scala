package graphql.types.user

import graphql.types.FromAndToInternal
import graphql.types.util.NonEmptyList
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

import java.util.UUID

@JsonCodec
case class SessionId(uuid: UUID)

object SessionId {

  implicit val sessionIdFromAndToInternal: FromAndToInternal[SessionId, services.user.SessionId] =
    FromAndToInternal.create(
      fromInternal = sessionId =>
        SessionId(
          uuid = sessionId.uuid
        ),
      toInternal = sessionId =>
        services.user.SessionId(
          uuid = sessionId.uuid
        )
    )

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
