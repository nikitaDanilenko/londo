package services.dashboardEntry

import cats.effect.IO
import db.daos.dashboardEntry.DashboardEntryKey
import db.{ DashboardId, ProjectId }
import utils.date.DateUtil

case class DashboardEntryCreation(
    dashboardId: DashboardId,
    projectId: ProjectId
)

object DashboardEntryCreation {

  def create(dashboardEntryCreation: DashboardEntryCreation): IO[DashboardEntry] =
    for {
      now <- DateUtil.now
    } yield DashboardEntry(
      key = DashboardEntryKey(
        dashboardId = dashboardEntryCreation.dashboardId,
        projectId = dashboardEntryCreation.projectId
      ),
      createdAt = now
    )

}
