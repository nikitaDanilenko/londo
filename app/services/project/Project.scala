package services.project

import services.task.Task
import services.user.UserId

case class Project(
    id: ProjectId,
    plainTasks: Vector[Task.Plain],
    projectReferenceTasks: Vector[Task.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    parentProjectId: Option[ProjectId],
    flatIfSingleTask: Boolean,
    readAccessors: ProjectAccess[AccessKind.Read],
    writeAccessors: ProjectAccess[AccessKind.Write]
)
