package services.user

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class UserId(uuid: UUID) extends AnyVal
