package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.Visibility
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec
case class DashboardUpdate(
    header: String,
    description: Option[String],
    visibility: Visibility
)

object DashboardUpdate {

  implicit val toInternal: Transformer[DashboardUpdate, services.dashboard.Update] =
    Transformer
      .define[DashboardUpdate, services.dashboard.Update]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[DashboardUpdate] =
    deriveInputObjectType[DashboardUpdate]()

}
