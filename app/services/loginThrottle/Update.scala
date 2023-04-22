package services.loginThrottle

import io.scalaland.chimney.dsl._

import java.time.LocalDateTime

case class Update(
    failedAttempts: Int,
    lastAttemptAt: LocalDateTime
)

object Update {

  def update(loginThrottle: LoginThrottle, update: Update): LoginThrottle =
    loginThrottle
      .patchUsing(update)

}
