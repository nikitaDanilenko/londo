package services.task

import errors.ServerError
import services.project.ProjectId
import services.task
import spire.math.Natural

object Task {

  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Natural
  )

  object Plain {

    def fromRow(taskRow: db.models.PlainTask): ServerError.Valid[Plain] =
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

    def toRow(projectId: ProjectId, plainTask: Plain): db.models.PlainTask =
      db.models.PlainTask(
        id = plainTask.id.uuid,
        projectId = projectId.uuid,
        name = plainTask.name,
        unit = plainTask.unit,
        kindId = TaskKind.toRow(plainTask.taskKind).id,
        reached = asBigDecimal(plainTask.progress.reached),
        reachable = asBigDecimal(plainTask.progress.reachable),
        weight = plainTask.weight.intValue
      )

  }

  case class ProjectReference(
      id: TaskId,
      projectReference: ProjectId,
      weight: Natural
  )

  object ProjectReference {

    def fromRow(taskRow: db.models.ProjectReferenceTask): ServerError.Valid[ProjectReference] =
      nonNegativeWeight(taskRow.weight).map { weight =>
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
        weight = projectReferenceTask.weight.intValue
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
