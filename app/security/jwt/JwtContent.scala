package security.jwt

import io.circe.generic.JsonCodec
import graphql.types.user.{ SessionId, UserId }

@JsonCodec
case class JwtContent(
    userId: UserId,
    sessionId: SessionId
)
