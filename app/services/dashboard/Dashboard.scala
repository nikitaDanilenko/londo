package services.dashboard

import services.access.{ Access, AccessKind }
import services.project.Project
import services.user.UserId

case class Dashboard(
    id: DashboardId,
    projects: Vector[Project],
    header: String,
    description: Option[String],
    userId: UserId,
    readAccessors: Access[AccessKind.Read],
    writeAccessors: Access[AccessKind.Write]
)
