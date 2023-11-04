package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class DashboardStatistics(
    reached: WithSimulationNatural,
    reachable: WithoutSimulation,
    absoluteMeans: WithSimulationRational,
    relativeMeans: WithSimulationRational
)

object DashboardStatistics {

  implicit val fromInternal: Transformer[processing.statistics.dashboard.DashboardStatistics, DashboardStatistics] =
    Transformer
      .define[processing.statistics.dashboard.DashboardStatistics, DashboardStatistics]
      .buildTransformer

  implicit val objectType: ObjectType[Unit, DashboardStatistics] = deriveObjectType[Unit, DashboardStatistics]()

}
