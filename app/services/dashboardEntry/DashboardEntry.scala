package services.dashboardEntry

import db.daos.dashboardEntry.DashboardEntryKey

import java.util.Date

case class DashboardEntry(
    key: DashboardEntryKey,
    createdAt: Date
)
