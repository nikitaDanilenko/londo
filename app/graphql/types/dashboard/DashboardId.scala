package graphql.types.dashboard

import graphql.types.FromAndToInternal
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID

@JsonCodec
case class DashboardId(uuid: UUID)

object DashboardId {

  implicit lazy val dashboardIdFromAndToInternal: FromAndToInternal[DashboardId, services.dashboard.DashboardId] =
    FromAndToInternal.create(
      fromInternal = dashboardId =>
        DashboardId(
          uuid = dashboardId.uuid
        ),
      toInternal = dashboardId =>
        services.dashboard.DashboardId(
          uuid = dashboardId.uuid
        )
    )

  implicit val dashboardIdObjectType: ObjectType[Unit, DashboardId] = deriveObjectType[Unit, DashboardId]()

  implicit val dashboardIdInputObjectType: InputObjectType[DashboardId] = deriveInputObjectType[DashboardId](
    InputObjectTypeName("DashboardIdInput")
  )

  implicit lazy val dashboardIdFromInput: FromInput[DashboardId] = circeDecoderFromInput[DashboardId]
}
