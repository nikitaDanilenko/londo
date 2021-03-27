package services.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UserDetails(
    firstName: Option[String],
    lastName: Option[String],
    description: Option[String]
)

object UserDetails {

  val default: UserDetails = UserDetails(
    firstName = None,
    lastName = None,
    description = None
  )

  def fromRow(userDetailsRow: db.models.UserDetails): UserDetails =
    UserDetails(
      firstName = userDetailsRow.firstName,
      lastName = userDetailsRow.lastName,
      description = userDetailsRow.description
    )

}
