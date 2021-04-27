package services.task

import cats.effect.IO
import db.keys.ProjectId
import services.project.Progress
import spire.math.Natural
import utils.random.RandomGenerator

sealed trait TaskCreation {
  def weight: Natural
}

object TaskCreation {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      override val weight: Natural
  ) extends TaskCreation

  case class ProjectReference(
      override val weight: Natural,
      projectReferenceId: ProjectId
  ) extends TaskCreation

  def create(taskCreation: TaskCreation): IO[Task] =
    RandomGenerator.randomUUID.map { uuid =>
      val taskId = TaskId(uuid)
      taskCreation match {
        case Plain(name, taskKind, unit, progress, weight) =>
          Task.Plain(
            id = taskId,
            name = name,
            taskKind = taskKind,
            unit = unit,
            progress = progress,
            weight = weight
          )
        case ProjectReference(weight, projectReferenceId) =>
          Task.ProjectReference(
            id = taskId,
            projectReference = projectReferenceId,
            weight = weight
          )
      }
    }

}
