package db.daos.dashboardEntry

import db.generated.Tables
import db.{ DAOActions, DashboardId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.DashboardEntryRow, DashboardEntryKey] {

  override val keyOf: Tables.DashboardEntryRow => DashboardEntryKey =
    DashboardEntryKey.of

  def findAllFor(dashboardId: DashboardId): DBIO[Seq[Tables.DashboardEntryRow]]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.DashboardEntryRow, Tables.DashboardEntry, DashboardEntryKey](
      Tables.DashboardEntry,
      (table, key) =>
        table.dashboardId === key.dashboardId.transformInto[UUID] && table.projectId === key.projectId
          .transformInto[UUID]
    ) with DAO {

      override def findAllFor(dashboardId: DashboardId): DBIO[Seq[Tables.DashboardEntryRow]] =
        Tables.DashboardEntry
          .filter(
            _.dashboardId === dashboardId.transformInto[UUID]
          )
          .result

    }

}
