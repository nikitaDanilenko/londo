package processing.statistics.dashboard

import spire.math.{ Natural, Rational }

case class Buckets(
    total: Map[Bucket, Natural],
    counting: Map[Bucket, Natural]
)
