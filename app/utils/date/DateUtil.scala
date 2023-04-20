package utils.date

import cats.effect.IO

import java.time.Instant
import java.util.Date
import scala.util.chaining._

object DateUtil {

  def now: IO[Date] = IO {
    Instant.now().pipe(Date.from)
  }

}
