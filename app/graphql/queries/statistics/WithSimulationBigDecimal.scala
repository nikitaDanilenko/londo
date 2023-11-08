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
    counting: BigDecimal,
    simulatedTotal: BigDecimal,
    simulatedcounting: BigDecimal
)

object WithSimulationBigDecimal {

  implicit val fromInternal: Transformer[(WithSimulation[spire.math.Rational], Positive), WithSimulationBigDecimal] = {
    case (withSimulation, numberOfDecimalPlaces) =>
      val toBigDecimal = MathUtil.rationalToBigDecimal(numberOfDecimalPlaces)
      WithSimulationBigDecimal(
        total = toBigDecimal(withSimulation.total),
        counting = toBigDecimal(withSimulation.counting),
        simulatedTotal = toBigDecimal(withSimulation.simulatedTotal),
        simulatedcounting = toBigDecimal(withSimulation.simulatedcounting)
      )

  }

  implicit val objectType: ObjectType[Unit, WithSimulationBigDecimal] =
    deriveObjectType[Unit, WithSimulationBigDecimal]()

}
