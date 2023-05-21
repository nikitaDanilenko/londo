package services.email

case class EmailParameters(
    to: Seq[String],
    cc: Seq[String],
    subject: String,
    message: String
)
