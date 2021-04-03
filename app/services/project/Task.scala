package services.project

case class Task(
    id: TaskId,
    projectId: ProjectId,
    name: String,
    unit: Option[String],
    kind: TaskKind,
    progress: Progress
)
