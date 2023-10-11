package graphql.mutations.dashboard.inputs

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class SimulationUpdate(
    reachedModifier: Int
)

object SimulationUpdate {

  implicit val toInternal: Transformer[SimulationUpdate, services.simulation.Update] =
    Transformer
      .define[SimulationUpdate, services.simulation.Update]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[SimulationUpdate] = deriveInputObjectType[SimulationUpdate]()

}
