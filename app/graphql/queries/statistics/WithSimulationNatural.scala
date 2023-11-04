package graphql.queries.statistics

import graphql.types.util.Natural
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class WithSimulationNatural(
    total: Natural,
    counted: Natural,
    simulatedTotal: Natural,
    simulatedCounted: Natural
)

object WithSimulationNatural {

  implicit val fromInternal
      : Transformer[processing.statistics.WithSimulation[spire.math.Natural], WithSimulationNatural] =
    Transformer
      .define[processing.statistics.WithSimulation[spire.math.Natural], WithSimulationNatural]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, WithSimulationNatural] = deriveObjectType[Unit, WithSimulationNatural]()

}
