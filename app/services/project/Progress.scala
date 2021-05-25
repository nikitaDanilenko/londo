package services.project

import algebra.ring.AdditiveMonoid
import io.circe.Encoder
import spire.algebra.Order
import spire.math.{ Natural, Rational }
import utils.json.CirceUtil.instances._
import io.circe.generic.semiauto._
import io.circe.syntax._

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

  implicit val progressEncoder: Encoder[Progress] = Encoder.instance {
    case impl: Impl => impl.asJson
  }

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

  private object Impl {
    implicit val implEncoder: Encoder[Impl] = deriveEncoder[Impl]
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
