package services.task

import cats.syntax.contravariantSemigroupal._
import errors.ServerError
import math.Positive
import services.project.ProjectId
import services.task
import spire.math.Natural
import utils.math.NaturalUtil

object Task {

  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Positive
  )

  object Plain {

    def fromRow(taskRow: db.models.PlainTask): ServerError.Valid[Plain] =
      (
        positiveWeight(taskRow.weight),
        ServerError.fromEither(asNatural(taskRow.reachable).flatMap(Positive.apply)),
        ServerError.fromEither(asNatural(taskRow.reached))
      )
        .mapN { (weight, reachable, reached) =>
          Plain(
            id = task.TaskId(uuid = taskRow.id),
            name = taskRow.name,
            taskKind = TaskKind.fromId(taskRow.kindId),
            unit = taskRow.unit,
            progress = Progress.fraction(
              reachable = reachable,
              reached = reached
            ),
            weight = weight
          )
        }

    def toRow(projectId: ProjectId, plainTask: Plain): db.models.PlainTask =
      db.models.PlainTask(
        id = plainTask.id.uuid,
        projectId = projectId.uuid,
        name = plainTask.name,
        unit = plainTask.unit,
        kindId = TaskKind.toRow(plainTask.taskKind).id,
        reached = asBigDecimal(plainTask.progress.reached),
        reachable = asBigDecimal(plainTask.progress.reachable.natural),
        weight = plainTask.weight.natural.intValue
      )

  }

  case class ProjectReference(
      id: TaskId,
      projectReference: ProjectId,
      weight: Positive
  )

  object ProjectReference {

    def fromRow(taskRow: db.models.ProjectReferenceTask): ServerError.Valid[ProjectReference] =
      positiveWeight(taskRow.weight).map { weight =>
        ProjectReference(
          id = task.TaskId(
            uuid = taskRow.id
          ),
          ProjectId(
            uuid = taskRow.projectReferenceId
          ),
          weight = weight
        )
      }

    def toRow(projectId: ProjectId, projectReferenceTask: ProjectReference): db.models.ProjectReferenceTask =
      db.models.ProjectReferenceTask(
        id = projectReferenceTask.id.uuid,
        projectId = projectId.uuid,
        projectReferenceId = projectReferenceTask.projectReference.uuid,
        weight = projectReferenceTask.weight.natural.intValue
      )

  }

  private def asNatural(bigDecimal: BigDecimal): ServerError.Or[Natural] =
    NaturalUtil.fromBigInt(bigDecimal.toBigInt)

  private def asBigDecimal(natural: Natural): BigDecimal =
    BigDecimal(natural.toBigInt)

  private def positiveWeight(weight: Int): ServerError.Valid[Positive] =
    ServerError.fromEither(Positive(Natural(weight)))

}
