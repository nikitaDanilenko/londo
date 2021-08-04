package services.dashboard

import services.project.ProjectId
import spire.math.Natural

case class ProjectWeightOnDashboard(
    projectId: ProjectId,
    weight: Natural
)

object ProjectWeightOnDashboard {

  def toDb(
      dashboardId: DashboardId,
      projectWeightOnDashboard: ProjectWeightOnDashboard
  ): db.models.DashboardProjectAssociation =
    db.models.DashboardProjectAssociation(
      dashboardId = dashboardId.uuid,
      projectId = projectWeightOnDashboard.projectId.uuid,
      weight = projectWeightOnDashboard.weight.intValue
    )

}
