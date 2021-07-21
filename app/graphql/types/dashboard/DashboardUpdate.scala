package graphql.types.dashboard

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class DashboardUpdate(
    header: String,
    description: Option[String],
    userId: UserId,
    flatIfSingleTask: Boolean
)

object DashboardUpdate {

  implicit val dashboardUpdateToInternal: ToInternal[DashboardUpdate, services.dashboard.DashboardUpdate] =
    dashboardUpdate =>
      services.dashboard.DashboardUpdate(
        header = dashboardUpdate.header,
        description = dashboardUpdate.description,
        userId = dashboardUpdate.userId.toInternal
      )

  implicit val dashboardUpdateInputObjectType: InputObjectType[DashboardUpdate] =
    deriveInputObjectType[DashboardUpdate]()

  implicit lazy val dashboardUpdateFromInput: FromInput[DashboardUpdate] = circeDecoderFromInput[DashboardUpdate]

}
