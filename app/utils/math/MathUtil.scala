package utils.math

import cats.Order

object MathUtil {

  def clamp[N: Order](min: N, max: N)(value: N): N =
    spire.math.min(spire.math.max(value, min), max)

}
