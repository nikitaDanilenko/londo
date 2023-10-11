package services.simulation

import db.generated.Tables
import db.{ DashboardId, ProjectId, TaskId }
import io.scalaland.chimney.Transformer
import utils.transformer.implicits._

case class Simulation(
    taskId: TaskId,
    projectId: ProjectId,
    dashboardId: DashboardId,
    reachedModifier: Int
)

object Simulation {

  implicit val fromDB: Transformer[Tables.SimulationRow, Simulation] =
    Transformer
      .define[Tables.SimulationRow, Simulation]
      .buildTransformer

  implicit val toDB: Transformer[Simulation, Tables.SimulationRow] =
    Transformer
      .define[Simulation, Tables.SimulationRow]
      .buildTransformer

}
