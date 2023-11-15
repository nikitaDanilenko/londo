package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class CountingBucket(
    bucket: Bucket,
    amount: Int
)

object CountingBucket {

  implicit val fromInternal
      : Transformer[(processing.statistics.dashboard.Bucket, spire.math.Natural), CountingBucket] = {
    case (bucket, amount) =>
      CountingBucket(
        bucket = bucket.transformInto[Bucket],
        amount = amount.intValue
      )
  }

  implicit val outputType: OutputType[CountingBucket] = deriveObjectType[Unit, CountingBucket]()

}
