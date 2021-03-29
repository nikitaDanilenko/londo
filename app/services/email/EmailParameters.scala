package services.email

case class EmailParameters(
    from: String,
    to: String,
    content: String
)
