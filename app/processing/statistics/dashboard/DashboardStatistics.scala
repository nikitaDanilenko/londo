package processing.statistics.dashboard

import spire.math.{ Natural, Rational }

case class DashboardStatistics(
    reached: WithSimulation[Natural],
    reachable: WithoutSimulation,
    absoluteMeans: WithSimulation[Rational],
    relativeMeans: WithSimulation[Rational]
    // todo: Revisit buckets
//    buckets: Buckets
)
