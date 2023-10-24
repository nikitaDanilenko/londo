package db.daos.simulation

import db.generated.Tables
import db.{ DAOActions, DashboardId, generated }
import io.scalaland.chimney.dsl._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.SimulationRow, SimulationKey] {
  override def keyOf: Tables.SimulationRow => SimulationKey = SimulationKey.of

  def findAllFor(dashboardId: DashboardId): DBIO[Seq[Tables.SimulationRow]]

  def findAllByTaskId(taskId: UUID): DBIO[Seq[Tables.SimulationRow]]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.SimulationRow, Tables.Simulation, SimulationKey](
      Tables.Simulation,
      (table, key) =>
        table.taskId === key.taskId.transformInto[UUID] &&
          table.dashboardId === key.dashboardId.transformInto[UUID]
    ) with DAO {

      override def findAllFor(dashboardId: DashboardId): DBIO[Seq[Tables.SimulationRow]] =
        Tables.Simulation
          .filter(
            _.dashboardId === dashboardId.transformInto[UUID]
          )
          .result

      override def findAllByTaskId(taskId: UUID): DBIO[Seq[generated.Tables.SimulationRow]] =
        Tables.Simulation
          .filter(_.taskId === taskId)
          .result

    }

}
