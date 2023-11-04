package processing.statistics.dashboard

case class TaskWithSimulation(
    task: Task,
    simulation: Option[BigInt]
)
