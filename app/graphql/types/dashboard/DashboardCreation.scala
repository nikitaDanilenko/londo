package graphql.types.dashboard

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
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

  implicit val projectCreationInputObjectType: InputObjectType[DashboardCreation] =
    deriveInputObjectType[DashboardCreation]()

  implicit lazy val projectCreationFromInput: FromInput[DashboardCreation] = circeDecoderFromInput[DashboardCreation]

}
