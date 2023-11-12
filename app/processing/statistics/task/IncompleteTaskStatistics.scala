package processing.statistics.task

import spire.math.Rational

case class IncompleteTaskStatistics(
    mean: Rational,
    total: After,
    counting: After
)
