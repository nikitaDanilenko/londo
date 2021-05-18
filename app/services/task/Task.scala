package services.task

import db.keys
import db.keys.ProjectId
import errors.ServerError
import services.project.Progress
import services.task
import spire.math.Natural

sealed trait Task {
  def id: TaskId
  def weight: Natural
}

object Task {

  case class Plain(
      override val id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      override val weight: Natural
  ) extends Task

  case class ProjectReference(
      override val id: TaskId,
      projectReference: ProjectId,
      override val weight: Natural
  ) extends Task

  def fromPlainTaskRow(taskRow: db.models.PlainTask): ServerError.Valid[Plain] =
    nonNegativeWeight(taskRow.weight)
      .map { weight =>
        Plain(
          id = task.TaskId(uuid = taskRow.id),
          name = taskRow.name,
          taskKind = TaskKind.fromId(taskRow.kindId),
          unit = taskRow.unit,
          progress = Progress.fraction(asNatural(taskRow.reachable), asNatural(taskRow.reached)),
          weight = weight
        )
      }

  def fromProjectReferenceRow(taskRow: db.models.ProjectReferenceTask): ServerError.Valid[ProjectReference] =
    nonNegativeWeight(taskRow.weight).map { weight =>
      ProjectReference(
        id = task.TaskId(
          uuid = taskRow.id
        ),
        keys.ProjectId(
          uuid = taskRow.projectReferenceId
        ),
        weight = weight
      )
    }

  def toRow(projectId: ProjectId, task: Task): Either[db.models.PlainTask, db.models.ProjectReferenceTask] =
    task match {
      case Plain(id, name, taskKind, unit, progress, weight) =>
        Left(
          db.models.PlainTask(
            id = id.uuid,
            projectId = projectId.uuid,
            name = name,
            unit = unit,
            kindId = TaskKind.toRow(taskKind).id,
            reached = asBigDecimal(progress.reached),
            reachable = asBigDecimal(progress.reachable),
            weight = weight.intValue
          )
        )
      case ProjectReference(id, projectReference, weight) =>
        Right(
          db.models.ProjectReferenceTask(
            id = id.uuid,
            projectId = projectId.uuid,
            projectReferenceId = projectReference.uuid,
            weight = weight.intValue
          )
        )
    }

  private def asNatural(bigDecimal: BigDecimal): Natural =
    Natural(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

  private def nonNegativeWeight(weight: Int): ServerError.Valid[Natural] =
    ServerError.fromCondition(
      weight >= 0,
      ServerError.Task.NegativeWeight,
      Natural(weight)
    )

}
