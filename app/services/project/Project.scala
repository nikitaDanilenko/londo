package services.project

import services.access.{ Access, AccessKind }
import services.task.Task
import services.user.UserId

case class Project(
    id: ProjectId,
    plainTasks: Vector[Task.Plain],
    projectReferenceTasks: Vector[Task.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean,
    readAccessors: Access[AccessKind.Read],
    writeAccessors: Access[AccessKind.Write]
)
