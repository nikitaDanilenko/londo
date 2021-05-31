package security.jwt

import io.circe.generic.JsonCodec
import graphql.types.user.UserId

@JsonCodec
case class JwtContent(
    userId: UserId
)
