package services.dashboardEntry

import db.generated.Tables
import db.{ DashboardId, ProjectId }
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class DashboardEntry(
    projectId: ProjectId,
    createdAt: LocalDateTime
)

object DashboardEntry {

  implicit val fromDB: Transformer[Tables.DashboardEntryRow, DashboardEntry] =
    Transformer
      .define[Tables.DashboardEntryRow, DashboardEntry]
      .buildTransformer

  implicit val toDB: Transformer[(DashboardEntry, DashboardId), Tables.DashboardEntryRow] = {
    case (dashboardEntry, dashboardId) =>
      Tables.DashboardEntryRow(
        dashboardId = dashboardId.transformInto[UUID],
        projectId = dashboardEntry.projectId.transformInto[UUID],
        createdAt = dashboardEntry.createdAt.transformInto[java.sql.Timestamp]
      )
  }

}
