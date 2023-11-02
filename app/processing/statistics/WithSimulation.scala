package processing.statistics

import spire.math.Natural

case class WithSimulation[A](
    total: A,
    counted: A,
    simulatedTotal: A,
    simulatedCounted: A
)
