package graphql.types.simulation

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._

@JsonCodec
case class Simulation(
    reachedModifier: BigInt
)

object Simulation {

  implicit val fromInternal: Transformer[services.simulation.Simulation, Simulation] =
    Transformer
      .define[services.simulation.Simulation, Simulation]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, Simulation] = deriveObjectType[Unit, Simulation]()

}
