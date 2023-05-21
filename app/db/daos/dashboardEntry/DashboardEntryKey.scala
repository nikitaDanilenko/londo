package db.daos.dashboardEntry

import db.generated.Tables
import db.{ DashboardId, ProjectId }
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

case class DashboardEntryKey(
    dashboardId: DashboardId,
    projectId: ProjectId
)

object DashboardEntryKey {

  def of(row: Tables.DashboardEntryRow): DashboardEntryKey =
    DashboardEntryKey(
      row.dashboardId.transformInto[DashboardId],
      row.projectId.transformInto[ProjectId]
    )

}
