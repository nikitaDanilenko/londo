package services.task

import math.Positive
import services.project.ResolvedProject

object ResolvedTask {

  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Positive
  )

  case class ProjectReference(
      id: TaskId,
      project: ResolvedProject,
      weight: Positive
  )

}
