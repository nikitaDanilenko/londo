package db.daos.dashboard

import db.generated.Tables
import db.{ DAOActions, DashboardId, UserId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

trait DAO extends DAOActions[Tables.DashboardRow, DashboardKey] {

  override val keyOf: Tables.DashboardRow => DashboardKey = DashboardKey.of

  def allOf(userId: UserId, dashboardIds: Seq[DashboardId]): DBIO[Seq[Tables.DashboardRow]]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.DashboardRow, Tables.Dashboard, DashboardKey](
      Tables.Dashboard,
      (table, key) =>
        table.userId === key.userId.transformInto[UUID] && table.id === key.dashboardId.transformInto[UUID]
    ) with DAO {

      override def allOf(userId: UserId, dashboardIds: Seq[DashboardId]): DBIO[Seq[Tables.DashboardRow]] = {
        val untypedIds = dashboardIds.distinct.map(_.transformInto[UUID])
        Tables.Dashboard
          .filter(dashboard => dashboard.userId === userId.transformInto[UUID] && dashboard.id.inSetBind(untypedIds))
          .result
      }

    }

}
