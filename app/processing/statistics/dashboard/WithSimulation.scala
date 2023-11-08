package processing.statistics.dashboard

case class WithSimulation[A](
    total: A,
    counting: A,
    simulatedTotal: A,
    simulatedcounting: A
)
