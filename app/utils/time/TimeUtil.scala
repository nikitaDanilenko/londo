package utils.time

import cats.effect.IO

object TimeUtil {

  def nowSeconds: IO[Long] =
    IO {
      System.currentTimeMillis() / 1000
    }

}
