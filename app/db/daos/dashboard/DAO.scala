package db.daos.dashboard

import db.generated.Tables
import db.{DAOActions, UserId}
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

trait DAO extends DAOActions[Tables.DashboardRow, DashboardKey] {

  override val keyOf: Tables.DashboardRow => DashboardKey = DashboardKey.of

  def allInInterval(userId: UserId, requestInterval: RequestInterval): DBIO[Seq[Tables.DashboardRow]]

  def allOf(userId: UserId, mealIds: Seq[DashboardId]): DBIO[Seq[Tables.DashboardRow]]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.DashboardRow, Tables.Dashboard, DashboardKey](
      Tables.Dashboard,
      (table, key) => table.userId === key.userId.transformInto[UUID] && table.id === key.mealId.transformInto[UUID]
    ) with DAO {

      override def allInInterval(
          userId: UserId,
          requestInterval: RequestInterval
      ): DBIO[Seq[Tables.DashboardRow]] = {
        val dateFilter: Rep[java.sql.Date] => Rep[Boolean] =
          DBIOUtil.dateFilter(requestInterval.from, requestInterval.to)

        Tables.Dashboard
          .filter(meal =>
            meal.userId === userId.transformInto[UUID] &&
              dateFilter(meal.consumedOnDate)
          )
          .result
      }

      override def allOf(userId: UserId, mealIds: Seq[DashboardId]): DBIO[Seq[Tables.DashboardRow]] = {
        val untypedIds = mealIds.distinct.map(_.transformInto[UUID])
        Tables.Dashboard
          .filter(meal => meal.userId === userId.transformInto[UUID] && meal.id.inSetBind(untypedIds))
          .result
      }

    }

}
