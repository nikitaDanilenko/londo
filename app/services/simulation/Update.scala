package services.simulation

import cats.effect.IO
import services.task.Progress
import utils.date.DateUtil

case class Update(
    reachedModifier: BigInt
)

object Update {

  def from(simulation: IncomingSimulation): Update =
    Update(
      reachedModifier = simulation.reachedModifier
    )

  def update(simulation: Simulation, progress: Progress, update: Update): IO[Simulation] =
    for {
      now <- DateUtil.now
    } yield simulation.copy(
      reachedModifier = Simulation.clampedModifier(progress, update.reachedModifier),
      updatedAt = Some(now)
    )

}
