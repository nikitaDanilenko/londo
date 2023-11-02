package processing.statistics

case class TaskWithSimulation(
    task: Task,
    simulation: Option[BigInt]
)
