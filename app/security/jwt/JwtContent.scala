package security.jwt

import io.circe.generic.JsonCodec
import services.user.UserId

@JsonCodec
case class JwtContent(
    userId: UserId
)
