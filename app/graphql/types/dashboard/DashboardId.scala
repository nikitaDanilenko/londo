package graphql.types.dashboard

import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

import java.util.UUID
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

@JsonCodec
case class DashboardId(uuid: UUID)

object DashboardId {

  implicit val toInternal: Transformer[DashboardId, db.DashboardId] =
    _.uuid.transformInto[db.DashboardId]

  implicit val fromInternal: Transformer[db.DashboardId, DashboardId] =
    DashboardId(_)

  implicit val objectType: ObjectType[Unit, DashboardId] = deriveObjectType[Unit, DashboardId]()

  implicit val inputObjectType: InputObjectType[DashboardId] = deriveInputObjectType[DashboardId](
    InputObjectTypeName("DashboardIdInput")
  )

}
