package services.user

case class PasswordParameters(
    hash: String,
    salt: String,
    iterations: Int
)
