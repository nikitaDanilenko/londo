package services.dashboardEntry

import db.daos.dashboardEntry.DashboardEntryKey

import java.time.LocalDateTime

case class DashboardEntry(
    key: DashboardEntryKey,
    createdAt: LocalDateTime
)
