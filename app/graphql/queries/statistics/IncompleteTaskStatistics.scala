package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

import java.math.MathContext

case class IncompleteTaskStatistics(
    mean: BigDecimal,
    total: After,
    counted: After
)

object IncompleteTaskStatistics {

  implicit val fromInternal
      : Transformer[(processing.statistics.task.IncompleteTaskStatistics, MathContext), IncompleteTaskStatistics] = {
    case (statistics, mathContext) =>
      IncompleteTaskStatistics(
        mean = statistics.mean.toBigDecimal(mathContext),
        total = (statistics.total, mathContext).transformInto[After],
        counted = (statistics.counted, mathContext).transformInto[After]
      )

  }

  implicit val objectType: ObjectType[Unit, IncompleteTaskStatistics] =
    deriveObjectType[Unit, IncompleteTaskStatistics]()

}
