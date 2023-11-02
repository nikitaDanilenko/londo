package processing.statistics

import spire.math.Rational

case class Means(
    total: Rational,
    counted: Rational,
    simulatedTotal: Rational,
    simulatedCounted: Rational
)
