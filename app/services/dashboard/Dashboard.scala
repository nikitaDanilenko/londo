package services.dashboard

import services.access.{ Access, AccessKind }
import services.project.WeightedProject
import services.user.UserId

case class Dashboard(
    id: DashboardId,
    projects: Vector[WeightedProject],
    header: String,
    description: Option[String],
    userId: UserId,
    readAccessors: Access[AccessKind.Read],
    writeAccessors: Access[AccessKind.Write]
)
