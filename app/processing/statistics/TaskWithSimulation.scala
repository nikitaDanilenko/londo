package processing.statistics

case class TaskWithSimulation(
    task: services.task.Task,
    simulation: Option[BigInt]
)
