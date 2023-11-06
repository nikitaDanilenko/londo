package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import math.Positive
import processing.statistics.dashboard.WithSimulation
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._
import utils.math.MathUtil

case class WithSimulationBigDecimal(
    total: BigDecimal,
    counted: BigDecimal,
    simulatedTotal: BigDecimal,
    simulatedCounted: BigDecimal
)

object WithSimulationBigDecimal {

  implicit val fromInternal: Transformer[(WithSimulation[spire.math.Rational], Positive), WithSimulationBigDecimal] = {
    case (withSimulation, numberOfDecimalPlaces) =>
      val toBigDecimal = MathUtil.rationalToBigDecimal(numberOfDecimalPlaces)
      WithSimulationBigDecimal(
        total = toBigDecimal(withSimulation.total),
        counted = toBigDecimal(withSimulation.counted),
        simulatedTotal = toBigDecimal(withSimulation.simulatedTotal),
        simulatedCounted = toBigDecimal(withSimulation.simulatedCounted)
      )

  }

  implicit val objectType: ObjectType[Unit, WithSimulationBigDecimal] =
    deriveObjectType[Unit, WithSimulationBigDecimal]()

}
