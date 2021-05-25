package services.task

import db.keys.ProjectId
import spire.math.Natural

sealed trait TaskUpdate {
  def weight: Natural
}

object TaskUpdate {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      override val weight: Natural
  ) extends TaskUpdate

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
      override val weight: Natural
  ) extends TaskUpdate

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
