package services.dashboardEntry

import db.DashboardId
import db.daos.dashboardEntry.DashboardEntryKey
import db.generated.Tables
import io.scalaland.chimney.Transformer

import java.time.LocalDateTime

case class DashboardEntry(
    key: DashboardEntryKey,
    createdAt: LocalDateTime
)

object DashboardEntry {
  implicit val fromDB: Transformer[Tables.DashboardEntryRow, DashboardEntry]              = ???
  implicit val toDB: Transformer[(DashboardEntry, DashboardId), Tables.DashboardEntryRow] = ???
}
