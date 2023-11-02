package graphql.queries.dashboard

import graphql.types.util.Natural
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class WithoutSimulation(
    total: Natural,
    counted: Natural
)

object WithoutSimulation {

  implicit val fromInternal: Transformer[processing.statistics.WithSimulation[spire.math.Natural], WithoutSimulation] =
    Transformer
      .define[processing.statistics.WithSimulation[spire.math.Natural], WithoutSimulation]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, WithoutSimulation] = deriveObjectType[Unit, WithoutSimulation]()

}
