package db.models

case class ProjectReferenceTask(
    id: java.util.UUID,
    projectId: java.util.UUID,
    projectReferenceId: java.util.UUID,
    weight: Int
)
