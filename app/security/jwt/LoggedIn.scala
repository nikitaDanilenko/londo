package security.jwt

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class LoggedIn(
    userId: UUID,
    sessionId: UUID
)
