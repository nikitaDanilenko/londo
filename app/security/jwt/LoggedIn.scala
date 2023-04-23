package security.jwt

import io.circe.generic.JsonCodec
import graphql.types.user.{ SessionId, UserId }

@JsonCodec
case class LoggedIn(
    userId: UserId,
    sessionId: SessionId
)
