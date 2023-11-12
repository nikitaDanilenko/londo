package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import math.Positive
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._
import utils.math.MathUtil

case class IncompleteTaskStatistics(
    mean: BigDecimal,
    total: After,
    counting: After
)

object IncompleteTaskStatistics {

  implicit val fromInternal
      : Transformer[(processing.statistics.task.IncompleteTaskStatistics, Positive), IncompleteTaskStatistics] = {
    case (statistics, numberOfDecimalPlaces) =>
      val toBigDecimal = MathUtil.rationalToBigDecimal(numberOfDecimalPlaces)
      IncompleteTaskStatistics(
        mean = toBigDecimal(statistics.mean),
        total = (statistics.total, numberOfDecimalPlaces).transformInto[After],
        counting = (statistics.counting, numberOfDecimalPlaces).transformInto[After]
      )

  }

  implicit val objectType: ObjectType[Unit, IncompleteTaskStatistics] =
    deriveObjectType[Unit, IncompleteTaskStatistics]()

}
