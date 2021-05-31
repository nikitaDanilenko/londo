package graphql.types.project

import graphql.types.task.Task
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import services.project.ProjectAccess

@JsonCodec
case class Project(
    id: ProjectId,
    plainTasks: Vector[Task.Plain],
    projectReferenceTasks: Vector[Task.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    parentProjectId: Option[ProjectId],
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object Project {

  implicit val projectObjectType: ObjectType[Unit, Project] = deriveObjectType[Unit, Project]()

  def fromInternal(project: services.project.Project): Project =
    Project(
      id = ProjectId.fromInternal(project.id),
      plainTasks = project.plainTasks.map(Task.Plain.fromInternal),
      projectReferenceTasks = project.projectReferenceTasks.map(Task.ProjectReference.fromInternal),
      name = project.name,
      description = project.description,
      ownerId = UserId.fromInternal(project.ownerId),
      parentProjectId = project.parentProjectId.map(ProjectId.fromInternal),
      flatIfSingleTask = project.flatIfSingleTask,
      readAccessors = accessorsFromInternal(project.readAccessors),
      writeAccessors = accessorsFromInternal(project.writeAccessors)
    )

  private def accessorsFromInternal[AK](projectAccess: ProjectAccess[AK]): Accessors =
    Accessors.fromInternal(services.project.Accessors.toRepresentation(projectAccess.accessors))

}
