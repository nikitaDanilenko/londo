package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UserDetails(
    firstName: Option[String],
    lastName: Option[String],
    description: Option[String]
)
