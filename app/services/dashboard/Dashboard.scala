package services.dashboard

import db.{ DashboardId, UserId }

import java.util.Date

case class Dashboard(
    id: DashboardId,
    header: String,
    description: Option[String],
    ownerId: UserId,
    publiclyVisible: Boolean,
    createdAt: Date,
    updatedAt: Option[Date]
)
