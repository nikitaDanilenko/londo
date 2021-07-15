package services.task

import services.project.ResolvedProject
import spire.math.Natural

object ResolvedTask {

  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Natural
  )

  case class ProjectReference(
      id: TaskId,
      project: ResolvedProject,
      weight: Natural
  )

}
