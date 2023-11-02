package processing.statistics

import spire.math.{ Natural, Rational }

case class DashboardStatistics(
    reached: WithSimulation[Natural],
    reachable: WithoutSimulation,
    absoluteMeans: WithSimulation[Rational],
    relativeMeans: WithSimulation[Rational]
)
