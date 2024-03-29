package graphql.mutations.dashboard.inputs

import graphql.types.dashboard.Visibility
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType

@JsonCodec(decodeOnly = true)
case class DashboardCreation(
    header: String,
    description: Option[String],
    visibility: Visibility
)

object DashboardCreation {

  implicit val toInternal: Transformer[DashboardCreation, services.dashboard.Creation] =
    Transformer
      .define[DashboardCreation, services.dashboard.Creation]
      .buildTransformer

  implicit val inputObjectType: InputObjectType[DashboardCreation] =
    deriveInputObjectType[DashboardCreation]()

}
