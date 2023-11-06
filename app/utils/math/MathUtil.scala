package utils.math

import cats.Order
import math.Positive
import spire.math.Rational

import java.math.RoundingMode

object MathUtil {

  def clamp[N: Order](min: N, max: N)(value: N): N =
    spire.math.min(spire.math.max(value, min), max)

  def rationalToBigDecimal(numberOfDecimalPlaces: Positive): Rational => BigDecimal =
    _.toBigDecimal(numberOfDecimalPlaces.natural.intValue, RoundingMode.HALF_EVEN)

}
