package graphql.queries.statistics

import graphql.types.util.Natural
import io.scalaland.chimney.Transformer
import processing.statistics.dashboard.WithSimulation
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class WithoutSimulation(
    total: Natural,
    counted: Natural
)

object WithoutSimulation {

  implicit val fromInternal: Transformer[WithSimulation[spire.math.Natural], WithoutSimulation] =
    Transformer
      .define[WithSimulation[spire.math.Natural], WithoutSimulation]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, WithoutSimulation] = deriveObjectType[Unit, WithoutSimulation]()

}
