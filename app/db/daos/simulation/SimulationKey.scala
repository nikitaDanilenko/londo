package db.daos.simulation

import db.generated.Tables
import db.{ DashboardId, TaskId }
import io.scalaland.chimney.dsl._

case class SimulationKey(
    taskId: TaskId,
    dashboardId: DashboardId
)

object SimulationKey {

  def of(row: Tables.SimulationRow): SimulationKey =
    SimulationKey(
      row.taskId.transformInto[TaskId],
      row.dashboardId.transformInto[DashboardId]
    )

}
