package services.project

import db.keys.ProjectId
import services.task.{ Task, TaskKind }
import spire.math.Natural

case class TaskUpdate(
    weight: Natural,
    update: Either[TaskUpdate.Plain, TaskUpdate.ProjectReference]
)

object TaskUpdate {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String]
  )

  case class ProjectReference(
      projectReferenceId: ProjectId
  )

  def applyToTask(task: Task, taskUpdate: TaskUpdate): Task =
    task match {
      case plain: Task.Plain =>
        val taskWithUpdatedWeight = plain.copy(weight = taskUpdate.weight)
        taskUpdate.update.fold(
          plainUpdate =>
            taskWithUpdatedWeight.copy(
              name = plainUpdate.name,
              taskKind = plainUpdate.taskKind,
              unit = plainUpdate.unit
            ),
          _ => taskWithUpdatedWeight
        )

      case projectReference: Task.ProjectReference =>
        val taskWithUpdatedWeight = projectReference.copy(weight = taskUpdate.weight)
        taskUpdate.update.fold(
          _ => taskWithUpdatedWeight,
          projectReferenceUpdate =>
            taskWithUpdatedWeight.copy(
              projectReference = projectReferenceUpdate.projectReferenceId
            )
        )

    }

}
