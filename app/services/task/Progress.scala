package services.task

import algebra.ring.AdditiveSemigroup
import math.Positive
import spire.algebra.{ MultiplicativeSemigroup, Order }
import spire.math.{ Natural, Rational }
import spire.syntax.multiplicativeMonoid._
import utils.math.NaturalUtil

sealed trait Progress {
  def reached: Natural
  def reachable: Positive

  def set(reachedValue: Natural): Progress

  final def value: Rational =
    Rational(
      n = reached.toBigInt,
      d = reachable.natural.toBigInt
    )

}

object Progress {

  private case class Impl(
      override val reached: Natural,
      override val reachable: Positive
  ) extends Progress {

    override def set(reachedValue: Natural): Progress =
      copy(
        reached = clampMax(
          value = reachedValue,
          max = reachable.natural
        )
      )

  }

  def zero(reachable: Positive): Progress =
    Impl(
      reached = Natural.zero,
      reachable = reachable
    )

  def fraction(reachable: Positive, reached: Natural): Progress =
    zero(reachable).set(reached)

  def missing(progress: Progress): Natural =
    progress.reachable.natural - progress.reached

  private def clampMax[A: Order](value: A, max: A): A =
    Order[A].min(max, value)

  private def unsafeFromRational(rational: Rational): Progress =
    Progress.fraction(
      // The conversion should be safe, since denominators are always positive
      reachable = NaturalUtil
        .fromBigInt(rational.denominator.toBigInt)
        .flatMap(Positive(_))
        .toOption
        .get,
      // This conversion is only safe for non-negative natural numbers
      reached = Natural(rational.numerator.toBigInt)
    )

  implicit val progressAdditiveSemigroup: AdditiveSemigroup[Progress] = { (x, y) =>
    unsafeFromRational(x.value + y.value)
  }

  implicit val progressMultiplicativeSemigroup: MultiplicativeSemigroup[Progress] = (x, y) =>
    fraction(x.reachable * y.reachable, x.reached * y.reached)

}
