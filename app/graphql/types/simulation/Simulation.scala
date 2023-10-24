package graphql.types.simulation

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }
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

  implicit val toInternal: Transformer[Simulation, services.simulation.IncomingSimulation] =
    Transformer
      .define[Simulation, services.simulation.IncomingSimulation]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[Simulation] =
    deriveInputObjectType[Simulation](InputObjectTypeName("SimulationInput"))

  implicit val objectType: ObjectType[Unit, Simulation] = deriveObjectType[Unit, Simulation]()

}
