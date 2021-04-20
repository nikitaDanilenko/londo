package db.keys

import io.circe.generic.JsonCodec

@JsonCodec
case class RegistrationTokenId(email: String) extends AnyVal
