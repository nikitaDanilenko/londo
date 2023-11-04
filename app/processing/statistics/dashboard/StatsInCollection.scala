package processing.statistics.dashboard

import spire.math.{Natural, Rational}

case class StatsInCollection(
    reached: Natural,
    reachedSimulated: Natural,
    reachable: Natural,
    meanAbsolute: Rational,
    meanAbsoluteSimulated: Rational,
    meanRelative: Rational,
    meanRelativeSimulated: Rational
)
