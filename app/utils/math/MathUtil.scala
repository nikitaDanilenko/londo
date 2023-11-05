package utils.math

import cats.Order
import math.Positive

import java.math.{ MathContext, RoundingMode }

object MathUtil {

  def clamp[N: Order](min: N, max: N)(value: N): N =
    spire.math.min(spire.math.max(value, min), max)

  def mathContextBy(numberOfDecimalPlaces: Positive): MathContext =
    new MathContext(numberOfDecimalPlaces.natural.toInt, RoundingMode.HALF_EVEN)

}
