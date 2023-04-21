package services.dashboard

import db.generated.Tables
import db.{ DashboardId, UserId }
import io.scalaland.chimney.Transformer

import java.time.LocalDateTime

case class Dashboard(
    id: DashboardId,
    header: String,
    description: Option[String],
    visibility: Visibility,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Dashboard {
  implicit val fromDB: Transformer[Tables.DashboardRow, Dashboard]         = ???
  implicit val toDB: Transformer[(Dashboard, UserId), Tables.DashboardRow] = ???
}
