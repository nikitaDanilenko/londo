package services.dashboardEntry

import db.{ DashboardId, ProjectId }
import db.daos.dashboardEntry.DashboardEntryKey
import db.generated.Tables
import io.scalaland.chimney.Transformer
import utils.transformer.implicits._

import java.time.LocalDateTime

case class DashboardEntry(
    projectId: ProjectId,
    createdAt: LocalDateTime
)

object DashboardEntry {

  implicit val fromDB: Transformer[Tables.DashboardEntryRow, DashboardEntry] =
    Transformer
      .define[Tables.DashboardEntryRow, DashboardEntry]
      .buildTransformer

  implicit val toDB: Transformer[(DashboardEntry, DashboardId), Tables.DashboardEntryRow] = ???
}
