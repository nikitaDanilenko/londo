package services.task

import algebra.ring.AdditiveMonoid
import spire.algebra.Order
import spire.math.{ Natural, Rational }

sealed trait Progress {
  def reached: Natural
  def reachable: Natural

  def set(reachedValue: Natural): Progress

  final def value: Rational =
    Rational(
      n = reached.toBigInt,
      d = reachable.toBigInt
    )

}

object Progress {

  private case class Impl(
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
    Impl(
      reached = Natural.zero,
      reachable = reachable
    )

  def fraction(reachable: Natural, reached: Natural): Progress =
    zero(reachable).set(reached)

  private def clampMax[A: Order: AdditiveMonoid](value: A, max: A): A =
    Order[A].min(max, value)

}
