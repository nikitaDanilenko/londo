package services.dashboard

import db.{ DashboardId, UserId }

import java.time.LocalDateTime

case class Dashboard(
    id: DashboardId,
    header: String,
    description: Option[String],
    ownerId: UserId,
    visibility: Visibility,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)
