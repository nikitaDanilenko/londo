package services.project

import cats.syntax.contravariantSemigroupal._
import db.keys
import db.keys.{ ProjectId, TaskId }
import errors.ServerError
import spire.math.Natural

sealed trait Task {
  def id: TaskId
}

object Task {

  case class Plain(
      override val id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Int
  ) extends Task

  case class ProjectReference(
      override val id: TaskId,
      projectReference: ProjectId
  ) extends Task

  def fromRow(taskRow: db.models.Task): ServerError.Valid[Task] = {

    val plain = (
      ServerError.fromEmpty(taskRow.projectReferenceId, ServerError.Task.Plain.NonEmptyProjectReference),
      ServerError.fromEither(taskRow.name.toRight(ServerError.Task.Plain.EmptyName)),
      ServerError.fromEither(taskRow.kindId.map(TaskKind.fromId).toRight(ServerError.Task.Plain.EmptyKind)),
      ServerError.fromEither(taskRow.reachable.toRight(ServerError.Task.Plain.EmptyReachable)),
      ServerError.fromEither(taskRow.reached.toRight(ServerError.Task.Plain.EmptyReached)),
      ServerError.fromEither(taskRow.weight.toRight(ServerError.Task.Plain.EmptyWeight))
    ).mapN { (_, name, taskKind, reachable, reached, weight) =>
      Plain(
        id = keys.TaskId(projectId = ProjectId(taskRow.projectId), uuid = taskRow.id),
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
      ServerError.fromEmpty(taskRow.weight, ServerError.Task.ProjectReference.NonEmptyWeight),
      ServerError.fromEmpty(taskRow.unit, ServerError.Task.ProjectReference.NonEmptyUnit)
    ).mapN { (projectReferenceId, _, _, _, _, _, _) =>
      ProjectReference(
        id = TaskId(
          projectId = keys.ProjectId(taskRow.projectId),
          uuid = taskRow.id
        ),
        keys.ProjectId(projectReferenceId)
      )
    }

    plain.findValid(projectReference)
  }

  def toRow(task: Task): db.models.Task =
    task match {
      case Plain(id, name, taskKind, unit, progress, weight) =>
        db.models.Task(
          id = id.uuid,
          projectId = id.projectId.uuid,
          projectReferenceId = None,
          name = Some(name),
          unit = unit,
          kindId = Some(TaskKind.toRow(taskKind).id),
          reached = Some(asBigDecimal(progress.reached)),
          reachable = Some(asBigDecimal(progress.reachable)),
          weight = Some(weight)
        )
      case ProjectReference(id, projectReference) =>
        db.models.Task(
          id = id.uuid,
          projectId = id.projectId.uuid,
          projectReferenceId = Some(projectReference.uuid),
          name = None,
          unit = None,
          kindId = None,
          reached = None,
          reachable = None,
          weight = None
        )
    }

  private def asNatural(bigDecimal: BigDecimal): Natural =
    Natural(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

}
