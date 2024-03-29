package math

import algebra.ring.AdditiveSemigroup
import errors.{ ErrorContext, ServerError }
import spire.algebra.MultiplicativeMonoid
import spire.math.Natural

sealed abstract case class Positive(
    natural: Natural
)

object Positive {

  def apply(natural: Natural): ServerError.Or[Positive] = {
    Either.cond(!natural.isZero, new Positive(natural) {}, ErrorContext.Conversion.PositiveNatural.asServerError)
  }

  def nextOf(natural: Natural): Positive = new Positive(natural + Natural.one) {}

  implicit val positiveAdditiveSemigroup: AdditiveSemigroup[Positive] = (x, y) => new Positive(x.natural + y.natural) {}

  implicit val positiveMultiplicativeMonoid: MultiplicativeMonoid[Positive] = new MultiplicativeMonoid[Positive] {
    override def one: Positive = new Positive(Natural.one) {}

    override def times(x: Positive, y: Positive): Positive =
      new Positive(x.natural * y.natural) {}

  }

}
