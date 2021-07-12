package services.task

import cats.effect.IO
import services.project.ProjectId
import spire.math.Natural
import utils.random.RandomGenerator

object TaskCreation {

  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Natural
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
      weight: Natural,
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
