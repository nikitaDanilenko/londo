package utils.math

import cats.Order
import math.Positive
import spire.math.{ Rational, SafeLong }

import java.math.{ MathContext, RoundingMode }

object MathUtil {

  def clamp[N: Order](min: N, max: N)(value: N): N =
    spire.math.min(spire.math.max(value, min), max)

  def rationalToBigDecimal(numberOfDecimalPlaces: Positive): Rational => BigDecimal = { r =>
    val denominator = SafeLong(10).pow(numberOfDecimalPlaces.natural.intValue)
    r.roundTo(denominator).toBigDecimal(MathContext.DECIMAL128)
  }

}
