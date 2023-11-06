package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import processing.statistics.dashboard.WithSimulation
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._

import java.math.MathContext

case class WithSimulationBigDecimal(
    total: BigDecimal,
    counted: BigDecimal,
    simulatedTotal: BigDecimal,
    simulatedCounted: BigDecimal
)

object WithSimulationBigDecimal {

  implicit val fromInternal
      : Transformer[(WithSimulation[spire.math.Rational], MathContext), WithSimulationBigDecimal] = {
    case (withSimulation, mathContext) =>
      WithSimulationBigDecimal(
        total = withSimulation.total.toBigDecimal(mathContext),
        counted = withSimulation.counted.toBigDecimal(mathContext),
        simulatedTotal = withSimulation.simulatedTotal.toBigDecimal(mathContext),
        simulatedCounted = withSimulation.simulatedCounted.toBigDecimal(mathContext)
      )

  }

  implicit val objectType: ObjectType[Unit, WithSimulationBigDecimal] =
    deriveObjectType[Unit, WithSimulationBigDecimal]()

}
