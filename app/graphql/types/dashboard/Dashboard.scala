package graphql.types.dashboard

import graphql.types.FromInternal
import graphql.types.FromInternal.syntax._
import graphql.types.access.Accessors
import graphql.types.project.WeightedProject
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class Dashboard(
    id: DashboardId,
    projects: Vector[WeightedProject],
    header: String,
    description: Option[String],
    userId: UserId,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object Dashboard {

  implicit val projectFromInternal: FromInternal[Dashboard, services.dashboard.Dashboard] = { dashboard =>
    Dashboard(
      id = dashboard.id.fromInternal,
      projects = dashboard.projects.map(_.fromInternal),
      header = dashboard.header,
      description = dashboard.description,
      userId = dashboard.userId.fromInternal,
      readAccessors = Accessors.fromInternalAccess(dashboard.readAccessors),
      writeAccessors = Accessors.fromInternalAccess(dashboard.writeAccessors)
    )
  }

  implicit val dashboardObjectType: ObjectType[Unit, Dashboard] = deriveObjectType[Unit, Dashboard]()

}
