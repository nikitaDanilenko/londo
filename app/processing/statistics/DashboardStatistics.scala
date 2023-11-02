package processing.statistics

case class DashboardStatistics(
    total: Progress,
    counted: Progress,
    absoluteMeans: Means,
    relativeMeans: Means
)
