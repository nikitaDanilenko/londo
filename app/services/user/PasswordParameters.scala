package services.user

import spire.math.Natural

case class PasswordParameters(
    hash: String,
    salt: String,
    iterations: Natural
)
