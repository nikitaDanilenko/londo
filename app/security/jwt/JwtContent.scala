package security.jwt

import db.keys.UserId
import io.circe.generic.JsonCodec

@JsonCodec
case class JwtContent(
    userId: UserId
)
