package services.dashboardEntry

import cats.effect.IO
import db.daos.dashboardEntry.DashboardEntryKey
import db.{ DashboardId, ProjectId }
import utils.date.DateUtil

case class Creation(
    projectId: ProjectId
)

object Creation {

  def create(dashboardEntryCreation: Creation): IO[DashboardEntry] =
    for {
      now <- DateUtil.now
    } yield DashboardEntry(
      projectId = dashboardEntryCreation.projectId,
      createdAt = now
    )

}
