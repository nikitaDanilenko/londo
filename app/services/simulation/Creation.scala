package services.simulation

import cats.effect.IO
import services.task.Progress
import utils.date.DateUtil

case class Creation(
    reachedModifier: BigInt
)

object Creation {

  def from(simulation: IncomingSimulation): Creation =
    Creation(
      reachedModifier = simulation.reachedModifier
    )

  def create(progress: Progress, creation: Creation): IO[Simulation] = {
    for {
      now <- DateUtil.now
    } yield Simulation(
      reachedModifier = Simulation.clampedModifier(progress, creation.reachedModifier),
      createdAt = now,
      updatedAt = None
    )
  }

}
