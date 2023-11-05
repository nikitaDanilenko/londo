package graphql.queries.statistics

import graphql.types.util.Rational
import io.scalaland.chimney.Transformer
import processing.statistics.dashboard.WithSimulation
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

// todo: The *internal* types should have Rational for maximum precision.
// For the transfer layer, we likely want to perform a trimmed conversion to BigDecimal.
// The number of decimal places should be a user setting that is either passed from the front end or fetched.
case class WithSimulationRational(
    total: Rational,
    counted: Rational,
    simulatedTotal: Rational,
    simulatedCounted: Rational
)

object WithSimulationRational {

  implicit val fromInternal: Transformer[WithSimulation[spire.math.Rational], WithSimulationRational] =
    Transformer
      .define[WithSimulation[spire.math.Rational], WithSimulationRational]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, WithSimulationRational] = deriveObjectType[Unit, WithSimulationRational]()

}
