package services.simulation

import cats.effect.IO
import utils.date.DateUtil

case class Creation(
    reachedModifier: BigInt
)

object Creation {

  def from(simulation: IncomingSimulation): Creation =
    Creation(
      reachedModifier = simulation.reachedModifier
    )

  def create(creation: Creation): IO[Simulation] = {
    for {
      now <- DateUtil.now
    } yield Simulation(
      reachedModifier = creation.reachedModifier,
      createdAt = now,
      updatedAt = None
    )
  }

}
