package db.models

case class UserDetails(
    userId: java.util.UUID,
    firstName: Option[String],
    lastName: Option[String],
    description: Option[String]
)
