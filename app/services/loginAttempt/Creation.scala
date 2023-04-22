package services.loginAttempt

object Creation {

  val create: LoginAttempt =
    LoginAttempt(
      failedAttempts = 0,
      lastSuccessfulLogin = None
    )

}
