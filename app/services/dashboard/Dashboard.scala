package services.dashboard

import db.generated.Tables
import db.{ DashboardId, UserId }
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class Dashboard(
    id: DashboardId,
    header: String,
    description: Option[String],
    visibility: Visibility,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Dashboard {

  implicit val fromDB: Transformer[Tables.DashboardRow, Dashboard] = row =>
    Dashboard(
      id = row.id.transformInto[DashboardId],
      header = row.header,
      description = row.description,
      visibility = Visibility.withName(row.visibility),
      createdAt = row.createdAt.transformInto[LocalDateTime],
      updatedAt = row.updatedAt.map(_.transformInto[LocalDateTime])
    )

  implicit val toDB: Transformer[(Dashboard, UserId), Tables.DashboardRow] = { case (dashboard, ownerId) =>
    Tables.DashboardRow(
      id = dashboard.id.transformInto[UUID],
      ownerId = ownerId.transformInto[UUID],
      header = dashboard.header,
      description = dashboard.description,
      visibility = dashboard.visibility.entryName,
      createdAt = dashboard.createdAt.transformInto[java.sql.Timestamp],
      updatedAt = dashboard.updatedAt.map(_.transformInto[java.sql.Timestamp])
    )
  }

}
