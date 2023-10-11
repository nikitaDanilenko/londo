package services.simulation

import db.generated.Tables
import db.{ DashboardId, ProjectId, TaskId }
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class Simulation(
    reachedModifier: Int,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Simulation {

  implicit val fromDB: Transformer[Tables.SimulationRow, Simulation] =
    Transformer
      .define[Tables.SimulationRow, Simulation]
      .buildTransformer

  implicit val toDB: Transformer[(Simulation, TaskId, DashboardId), Tables.SimulationRow] = {
    case (simulation, taskId, dashboardId) =>
      Tables.SimulationRow(
        taskId = taskId.transformInto[UUID],
        dashboardId = dashboardId.transformInto[UUID],
        reachedModifier = simulation.reachedModifier,
        createdAt = simulation.createdAt.transformInto[java.sql.Timestamp],
        updatedAt = simulation.updatedAt.map(_.transformInto[java.sql.Timestamp])
      )
  }

}
