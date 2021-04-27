package services.task

import cats.syntax.contravariantSemigroupal._
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

  def fromRow(taskRow: db.models.Task): ServerError.Valid[Task] = {
    val nonNegativeWeight =
      ServerError.fromCondition(
        taskRow.weight >= 0,
        ServerError.Task.NegativeWeight,
        Natural(taskRow.weight)
      )
    val plain = (
      ServerError.fromEmpty(taskRow.projectReferenceId, ServerError.Task.Plain.NonEmptyProjectReference),
      ServerError.fromEither(taskRow.name.toRight(ServerError.Task.Plain.EmptyName)),
      ServerError.fromEither(taskRow.kindId.map(TaskKind.fromId).toRight(ServerError.Task.Plain.EmptyKind)),
      ServerError.fromEither(taskRow.reachable.toRight(ServerError.Task.Plain.EmptyReachable)),
      ServerError.fromEither(taskRow.reached.toRight(ServerError.Task.Plain.EmptyReached)),
      nonNegativeWeight
    ).mapN { (_, name, taskKind, reachable, reached, weight) =>
      Plain(
        id = task.TaskId(uuid = taskRow.id),
        name = name,
        taskKind = taskKind,
        unit = taskRow.unit,
        progress = Progress.fraction(asNatural(reachable), asNatural(reached)),
        weight = weight
      )
    }

    val projectReference = (
      ServerError.fromEither(
        taskRow.projectReferenceId.toRight(ServerError.Task.ProjectReference.EmptyProjectReference)
      ),
      ServerError.fromEmpty(taskRow.name, ServerError.Task.ProjectReference.NonEmptyName),
      ServerError.fromEmpty(taskRow.kindId, ServerError.Task.ProjectReference.NonEmptyKind),
      ServerError.fromEmpty(taskRow.reachable, ServerError.Task.ProjectReference.NonEmptyReachable),
      ServerError.fromEmpty(taskRow.reached, ServerError.Task.ProjectReference.NonEmptyReached),
      ServerError.fromEmpty(taskRow.unit, ServerError.Task.ProjectReference.NonEmptyUnit),
      nonNegativeWeight
    ).mapN { (projectReferenceId, _, _, _, _, _, weight) =>
      ProjectReference(
        id = task.TaskId(
          uuid = taskRow.id
        ),
        keys.ProjectId(projectReferenceId),
        weight = weight
      )
    }

    plain.findValid(projectReference)
  }

  def toRow(projectId: ProjectId, task: Task): db.models.Task =
    task match {
      case Plain(id, name, taskKind, unit, progress, weight) =>
        db.models.Task(
          id = id.uuid,
          projectId = projectId.uuid,
          projectReferenceId = None,
          name = Some(name),
          unit = unit,
          kindId = Some(TaskKind.toRow(taskKind).id),
          reached = Some(asBigDecimal(progress.reached)),
          reachable = Some(asBigDecimal(progress.reachable)),
          weight = weight.intValue
        )
      case ProjectReference(id, projectReference, weight) =>
        db.models.Task(
          id = id.uuid,
          projectId = projectId.uuid,
          projectReferenceId = Some(projectReference.uuid),
          name = None,
          unit = None,
          kindId = None,
          reached = None,
          reachable = None,
          weight = weight.intValue
        )
    }

  private def asNatural(bigDecimal: BigDecimal): Natural =
    Natural(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

}
