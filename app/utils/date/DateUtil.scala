package utils.date

import cats.effect.IO

import java.time.LocalDateTime

object DateUtil {

  def now: IO[LocalDateTime] = IO {
    LocalDateTime.now()
  }

}
