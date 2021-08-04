package services.task

import cats.effect.IO
import math.Positive
import services.project.ProjectId
import utils.random.RandomGenerator

object TaskCreation {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Positive
  )

  object Plain {

    def create(plain: Plain): IO[Task.Plain] =
      RandomGenerator.randomUUID.map { uuid =>
        Task.Plain(
          id = TaskId(uuid),
          name = plain.name,
          taskKind = plain.taskKind,
          unit = plain.unit,
          progress = plain.progress,
          weight = plain.weight
        )
      }

  }

  case class ProjectReference(
      weight: Positive,
      projectReferenceId: ProjectId
  )

  object ProjectReference {

    def create(projectReference: ProjectReference): IO[Task.ProjectReference] =
      RandomGenerator.randomUUID.map { uuid =>
        Task.ProjectReference(
          id = TaskId(uuid),
          projectReference = projectReference.projectReferenceId,
          weight = projectReference.weight
        )
      }

  }

}
