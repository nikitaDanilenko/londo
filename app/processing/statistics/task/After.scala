package processing.statistics.task

import spire.math.Rational

case class After(
    one: Rational,
    completion: Rational,
    simulation: Option[Rational]
)

object After {

  val zero: After = After(
    one = Rational.zero,
    completion = Rational.zero,
    simulation = None
  )

}
