package services.project

import cats.syntax.contravariantSemigroupal._
import errors.ServerError
import spire.math.Natural

sealed trait Task {
  def id: TaskId
  def projectId: ProjectId
}

object Task {

  case class Plain(
      override val id: TaskId,
      override val projectId: ProjectId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Int
  ) extends Task

  case class ProjectReference(
      override val id: TaskId,
      override val projectId: ProjectId,
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
        id = TaskId(taskRow.id),
        projectId = ProjectId(taskRow.projectId),
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
        id = TaskId(taskRow.id),
        projectId = ProjectId(taskRow.projectId),
        ProjectId(projectReferenceId)
      )
    }

    plain.findValid(projectReference)
  }

  def toRow(task: Task): db.models.Task = ???
//    db.models.Task(
//      id = task.id.uuid,
//      projectId = task.projectId.uuid,
//      name = task.name,
//      unit = task.unit,
//      kindId = TaskKind.toRow(task.kind).id,
//      reached = asBigDecimal(task.progress.reached),
//      reachable = asBigDecimal(task.progress.reachable),
//      weight = task.weight
//    )

  private def asNatural(bigDecimal: BigDecimal): Natural =
    Natural(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

}
