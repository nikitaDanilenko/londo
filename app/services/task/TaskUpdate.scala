package services.task

import services.project.ProjectId
import spire.math.Natural

object TaskUpdate {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      weight: Natural
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
      weight: Natural
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
