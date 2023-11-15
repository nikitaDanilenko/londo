package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl.TransformerOps
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class Buckets(
    total: List[CountingBucket],
    counting: List[CountingBucket]
)

object Buckets {

  implicit val fromInternal: Transformer[processing.statistics.dashboard.Buckets, Buckets] = buckets =>
    Buckets(
      total = buckets.total.toList.transformInto[List[CountingBucket]],
      counting = buckets.counting.toList.transformInto[List[CountingBucket]]
    )

  implicit lazy val outputType: OutputType[Buckets] = deriveObjectType[Unit, Buckets]()

}
