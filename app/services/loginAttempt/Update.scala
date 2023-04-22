package services.loginAttempt

import io.scalaland.chimney.dsl._

import java.time.LocalDate

case class Update(
    failedAttempts: Int,
    lastSuccessfulLogin: Option[LocalDate]
)

object Update {

  def update(loginAttempt: LoginAttempt, update: Update): LoginAttempt =
    loginAttempt
      .patchUsing(update)

}
