package graphql.types.util

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec(encodeOnly = true)
case class Rational(
    numerator: BigInt,
    denominator: Natural
)

object Rational {

  implicit val fromInternal: Transformer[spire.math.Rational, Rational] = rational =>
    Rational(
      numerator = rational.numerator.toBigInt,
      denominator = spire.math.Natural(rational.denominator.toBigInt).transformInto[Natural]
    )

  implicit val objectType: ObjectType[Unit, Rational] = deriveObjectType[Unit, Rational]()

}
