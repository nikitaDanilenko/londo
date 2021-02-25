package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class User(
    id: UserId,
    nickname: String,
    email: String,
    passwordSalt: String,
    passwordHash: String
)
