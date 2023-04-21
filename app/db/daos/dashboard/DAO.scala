package db.daos.dashboard

import db.generated.Tables
import db.{ DAOActions, UserId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.DashboardRow, DashboardKey] {

  override val keyOf: Tables.DashboardRow => DashboardKey = DashboardKey.of

  def findAllFor(ownerId: UserId): DBIO[Seq[Tables.DashboardRow]]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.DashboardRow, Tables.Dashboard, DashboardKey](
      Tables.Dashboard,
      (table, key) =>
        table.ownerId === key.ownerId.transformInto[UUID] && table.id === key.dashboardId.transformInto[UUID]
    ) with DAO {

      override def findAllFor(ownerId: UserId): DBIO[Seq[Tables.DashboardRow]] = {
        Tables.Dashboard
          .filter(
            _.ownerId === ownerId.transformInto[UUID]
          )
          .result
      }

    }

}
