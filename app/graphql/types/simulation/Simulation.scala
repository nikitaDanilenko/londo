package graphql.types.simulation

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class Simulation(
    reachedModifier: Int
)

object Simulation {

  implicit val fromInternal: Transformer[services.simulation.Simulation, Simulation] =
    Transformer
      .define[services.simulation.Simulation, Simulation]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, Simulation] = deriveObjectType[Unit, Simulation]()

}
