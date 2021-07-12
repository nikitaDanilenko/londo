package services.user

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

  def toRow(userId: UserId, userDetails: UserDetails): db.models.UserDetails =
    db.models.UserDetails(
      userId = userId.uuid,
      firstName = userDetails.firstName,
      lastName = userDetails.lastName,
      description = userDetails.description
    )

}
