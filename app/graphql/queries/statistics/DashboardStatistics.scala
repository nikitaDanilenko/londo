package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import math.Positive
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class DashboardStatistics(
    reached: WithSimulationNatural,
    reachable: WithoutSimulation,
    absoluteMeans: WithSimulationBigDecimal,
    relativeMeans: WithSimulationBigDecimal,
    buckets: Buckets,
    tasks: Tasks,
    differenceTotalCounting: Int
)

object DashboardStatistics {

  implicit val fromInternal
      : Transformer[(processing.statistics.dashboard.DashboardStatistics, Positive), DashboardStatistics] = {
    case (statistics, numberOfDecimalPlaces) =>
      DashboardStatistics(
        reached = statistics.reached.transformInto[WithSimulationNatural],
        reachable = statistics.reachable.transformInto[WithoutSimulation],
        absoluteMeans = (statistics.absoluteMeans, numberOfDecimalPlaces).transformInto[WithSimulationBigDecimal],
        relativeMeans = (statistics.relativeMeans, numberOfDecimalPlaces).transformInto[WithSimulationBigDecimal],
        buckets = statistics.buckets.transformInto[Buckets],
        tasks = statistics.tasks.transformInto[Tasks],
        differenceTotalCounting = statistics.tasks.total - statistics.tasks.counting
      )
  }

  implicit val objectType: ObjectType[Unit, DashboardStatistics] = deriveObjectType[Unit, DashboardStatistics]()

}
