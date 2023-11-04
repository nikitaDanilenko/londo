package processing.statistics.dashboard

case class WithSimulation[A](
    total: A,
    counted: A,
    simulatedTotal: A,
    simulatedCounted: A
)
