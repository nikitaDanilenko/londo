package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

import java.math.MathContext

case class DashboardStatistics(
    reached: WithSimulationNatural,
    reachable: WithoutSimulation,
    absoluteMeans: WithSimulationBigDecimal,
    relativeMeans: WithSimulationBigDecimal
)

object DashboardStatistics {

  implicit val fromInternal
      : Transformer[(processing.statistics.dashboard.DashboardStatistics, MathContext), DashboardStatistics] = {
    case (statistics, mathContext) =>
      DashboardStatistics(
        reached = statistics.reached.transformInto[WithSimulationNatural],
        reachable = statistics.reachable.transformInto[WithoutSimulation],
        absoluteMeans = (statistics.absoluteMeans, mathContext).transformInto[WithSimulationBigDecimal],
        relativeMeans = (statistics.relativeMeans, mathContext).transformInto[WithSimulationBigDecimal]
      )
  }

  implicit val objectType: ObjectType[Unit, DashboardStatistics] = deriveObjectType[Unit, DashboardStatistics]()

}
