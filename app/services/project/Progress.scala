package services.project

import algebra.ring.AdditiveMonoid
import spire.algebra.Order
import spire.math.{ Natural, Rational }

import java.math.RoundingMode

sealed trait Progress {
  def reached: Natural
  def reachable: Natural

  def set(reachedValue: Natural): Progress

  final def decimalValue(precision: Natural): BigDecimal =
    Rational(
      n = reached.toBigInt,
      d = reachable.toBigInt
    )
      .toBigDecimal(precision.intValue, RoundingMode.HALF_UP)

}

object Progress {

  private case class ProgressImpl(
      override val reached: Natural,
      override val reachable: Natural
  ) extends Progress {

    override def set(reachedValue: Natural): Progress =
      copy(
        reached = clampMax(
          value = reachedValue,
          max = reachable
        )
      )

  }

  def zero(reachable: Natural): Progress =
    ProgressImpl(
      reached = Natural.zero,
      reachable = reachable
    )

  private def clampMax[A: Order: AdditiveMonoid](value: A, max: A): A =
    Order[A].min(max, value)

}
