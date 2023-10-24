package services.simulation

import db.generated.Tables
import db.{ DashboardId, TaskId }
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import services.task.Progress
import utils.math.MathUtil
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class Simulation(
    reachedModifier: BigInt,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Simulation {

  implicit val fromDB: Transformer[Tables.SimulationRow, Simulation] = row =>
    Simulation(
      reachedModifier = row.reachedModifier.toBigInt,
      createdAt = row.createdAt.transformInto[LocalDateTime],
      updatedAt = row.updatedAt.map(_.transformInto[LocalDateTime])
    )

  implicit val toDB: Transformer[(Simulation, TaskId, DashboardId), Tables.SimulationRow] = {
    case (simulation, taskId, dashboardId) =>
      Tables.SimulationRow(
        taskId = taskId.transformInto[UUID],
        dashboardId = dashboardId.transformInto[UUID],
        reachedModifier = BigDecimal(simulation.reachedModifier),
        createdAt = simulation.createdAt.transformInto[java.sql.Timestamp],
        updatedAt = simulation.updatedAt.map(_.transformInto[java.sql.Timestamp])
      )
  }

  def clampedModifier(progress: Progress, reachedModifier: BigInt): BigInt =
    MathUtil.clamp(
      min = -progress.reached.toBigInt,
      max = progress.reachable.natural.toBigInt - progress.reached.toBigInt
    )(reachedModifier)

}
