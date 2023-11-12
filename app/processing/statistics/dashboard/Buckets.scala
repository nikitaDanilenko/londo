package processing.statistics.dashboard

import spire.math.Rational

case class Buckets(
    values: Map[Bucket, Rational]
)
