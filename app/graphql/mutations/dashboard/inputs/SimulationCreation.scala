package graphql.mutations.dashboard.inputs

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class SimulationCreation(
    reachedModifier: Int
)

object SimulationCreation {

  implicit val toInternal: Transformer[SimulationCreation, services.simulation.Creation] =
    Transformer
      .define[SimulationCreation, services.simulation.Creation]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[SimulationCreation] = deriveInputObjectType[SimulationCreation]()

}
