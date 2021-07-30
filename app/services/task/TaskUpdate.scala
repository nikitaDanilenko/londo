package services.task

import math.Positive
import services.project.ProjectId

object TaskUpdate {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      weight: Positive
  )

  object Plain {

    def applyToTask(plainTask: Task.Plain, plainUpdate: Plain): Task.Plain =
      plainTask.copy(
        name = plainUpdate.name,
        taskKind = plainUpdate.taskKind,
        unit = plainUpdate.unit,
        weight = plainUpdate.weight
      )

  }

  case class ProjectReference(
      projectReferenceId: ProjectId,
      weight: Positive
  )

  object ProjectReference {

    def applyToTask(
        projectReferenceTask: Task.ProjectReference,
        projectReferenceUpdate: ProjectReference
    ): Task.ProjectReference =
      projectReferenceTask.copy(
        projectReference = projectReferenceUpdate.projectReferenceId,
        weight = projectReferenceUpdate.weight
      )

  }

}
