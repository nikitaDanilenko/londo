package graphql.queries.statistics

case class IncompleteTaskStatistics(
    mean: BigDecimal,
    afterOneTotal: BigDecimal,
    afterOneCounted: BigDecimal,
    afterCompletionTotal: BigDecimal,
    afterCompletionCounted: BigDecimal,
    withSimulationTotal: BigDecimal,
    withSimulationCounted: BigDecimal
)
