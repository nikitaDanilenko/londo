package services.project

import services.access.{ Access, AccessKind }
import services.task.ResolvedTask
import services.user.UserId

case class ResolvedProject(
    id: ProjectId,
    plainTasks: Vector[ResolvedTask.Plain],
    projectReferenceTasks: Vector[ResolvedTask.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean,
    readAccessors: Access[AccessKind.Read],
    writeAccessors: Access[AccessKind.Write]
)
