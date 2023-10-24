package services.simulation

import cats.effect.IO
import services.task.Progress
import utils.date.DateUtil
import utils.math.MathUtil

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
    } yield {
      val clampedModifier = MathUtil.clamp(
        min = -progress.reached.toBigInt,
        max = progress.reachable.natural.toBigInt - progress.reached.toBigInt
      )(creation.reachedModifier)
      Simulation(
        reachedModifier = clampedModifier,
        createdAt = now,
        updatedAt = None
      )
    }
  }

}
