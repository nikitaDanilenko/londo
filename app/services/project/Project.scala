package services.project

import db.keys.{ ProjectId, UserId }
import io.circe.generic.JsonCodec
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

object Project {

  @JsonCodec
  case class Representation(
      id: ProjectId,
      plainTasks: Vector[Task.Plain],
      projectReferenceTasks: Vector[Task.ProjectReference],
      name: String,
      description: Option[String],
      ownerId: UserId,
      parentProjectId: Option[ProjectId],
      flatIfSingleTask: Boolean,
      readAccessors: Accessors.Representation,
      writeAccessors: Accessors.Representation
  )

  def toRepresentation(project: Project): Representation =
    Representation(
      id = project.id,
      plainTasks = project.plainTasks,
      projectReferenceTasks = project.projectReferenceTasks,
      name = project.name,
      description = project.description,
      ownerId = project.ownerId,
      parentProjectId = project.parentProjectId,
      flatIfSingleTask = project.flatIfSingleTask,
      readAccessors = Accessors.toRepresentation(project.readAccessors.accessors),
      writeAccessors = Accessors.toRepresentation(project.writeAccessors.accessors)
    )

}
