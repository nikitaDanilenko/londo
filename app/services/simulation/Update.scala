package services.simulation

import cats.effect.IO
import utils.date.DateUtil

case class Update(
    reachedModifier: Int
)

object Update {

  def update(simulation: Simulation, update: Update): IO[Simulation] =
    for {
      now <- DateUtil.now
    } yield simulation.copy(
      reachedModifier = update.reachedModifier,
      updatedAt = Some(now)
    )

}
