package graphql.types.util

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

import utils.graphql.SangriaUtil.instances._

@JsonCodec(encodeOnly = true)
case class Rational(
    numerator: BigInt,
    denominator: Positive
)

object Rational {

  implicit val fromInternal: Transformer[spire.math.Rational, Rational] = rational =>
    Rational(
      numerator = rational.numerator.toBigInt,
      denominator = math.Positive
        .nextOf(spire.math.Natural(rational.denominator.toBigInt) - spire.math.Natural.one)
        .transformInto[Positive]
    )

  implicit val objectType: ObjectType[Unit, Rational] = deriveObjectType[Unit, Rational]()

}
