package services.dashboard

import services.user.UserId

case class DashboardUpdate(
    header: String,
    description: Option[String],
    userId: UserId
)

object DashboardUpdate {

  def applyToDashboard(dashboard: Dashboard, projectUpdate: DashboardUpdate): Dashboard =
    dashboard.copy(
      header = projectUpdate.header,
      description = projectUpdate.description,
      userId = projectUpdate.userId
    )

}
