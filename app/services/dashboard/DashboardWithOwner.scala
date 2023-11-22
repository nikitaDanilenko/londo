package services.dashboard

import db.UserId

case class DashboardWithOwner(
    dashboard: Dashboard,
    ownerId: UserId
)
