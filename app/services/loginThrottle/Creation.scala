package services.loginThrottle

import cats.effect.IO
import utils.date.DateUtil

object Creation {

  def create: IO[LoginThrottle] = for {
    now <- DateUtil.now
  } yield LoginThrottle(
    failedAttempts = 0,
    lastAttemptAt = now
  )

}
