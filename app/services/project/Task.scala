package services.project

import spire.math.Natural

case class Task(
    id: TaskId,
    projectId: ProjectId,
    name: String,
    unit: Option[String],
    kind: TaskKind,
    progress: Progress,
    weight: Int
)

object Task {

  def fromRow(taskRow: db.models.Task): Task =
    Task(
      id = TaskId(taskRow.id),
      projectId = ProjectId(taskRow.projectId),
      name = taskRow.name,
      unit = taskRow.unit,
      kind = TaskKind.fromId(taskRow.kindId),
      progress = Progress.zero(asNatural(taskRow.reachable)).set(asNatural(taskRow.reached)),
      weight = taskRow.weight
    )

  def toRow(task: Task): db.models.Task =
    db.models.Task(
      id = task.id.uuid,
      projectId = task.projectId.uuid,
      name = task.name,
      unit = task.unit,
      kindId = TaskKind.toRow(task.kind).id,
      reached = asBigDecimal(task.progress.reached),
      reachable = asBigDecimal(task.progress.reachable),
      weight = task.weight
    )

  private def asNatural(bigDecimal: BigDecimal): Natural =
    Natural(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

}
