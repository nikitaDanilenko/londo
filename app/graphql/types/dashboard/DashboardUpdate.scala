package graphql.types.dashboard

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
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

  implicit val dashboardUpdateInputObjectType: InputObjectType[DashboardUpdate] =
    deriveInputObjectType[DashboardUpdate]()

  implicit lazy val dashboardUpdateFromInput: FromInput[DashboardUpdate] = circeDecoderFromInput[DashboardUpdate]

}
