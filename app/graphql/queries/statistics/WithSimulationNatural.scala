package graphql.queries.statistics

import graphql.types.util.Natural
import io.scalaland.chimney.Transformer
import processing.statistics.dashboard.WithSimulation
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class WithSimulationNatural(
    total: Natural,
    counting: Natural,
    simulatedTotal: Natural,
    simulatedCounting: Natural
)

object WithSimulationNatural {

  implicit val fromInternal: Transformer[WithSimulation[spire.math.Natural], WithSimulationNatural] =
    Transformer
      .define[WithSimulation[spire.math.Natural], WithSimulationNatural]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, WithSimulationNatural] = deriveObjectType[Unit, WithSimulationNatural]()

}
