package db.models

case class LoginAttempt(
    userId: java.util.UUID,
    failedAttemptsSinceLastSuccessfulLogin: Int,
    lastSuccessfulLogin: Option[java.time.LocalDateTime]
)
