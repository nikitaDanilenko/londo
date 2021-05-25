package services.project

import db.keys.{ ProjectId, UserId }
import services.task.Task

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
