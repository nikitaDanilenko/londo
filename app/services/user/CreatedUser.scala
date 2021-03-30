package services.user

case class CreatedUser(
    user: User,
    passwordParameters: PasswordParameters
)
