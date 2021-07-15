package graphql.types.project

import graphql.types.FromInternal
import graphql.types.task.Task
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import FromInternal.syntax._
import services.access.Access

@JsonCodec
case class Project(
    id: ProjectId,
    plainTasks: Vector[Task.Plain],
    projectReferenceTasks: Vector[Task.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object Project {

  implicit val projectFromInternal: FromInternal[Project, services.project.Project] = {
    def accessorsFromInternal[AK](projectAccess: Access[AK]): Accessors =
      services.access.Accessors.toRepresentation(projectAccess.accessors).fromInternal
    project =>
      Project(
        id = project.id.fromInternal,
        plainTasks = project.plainTasks.map(_.fromInternal),
        projectReferenceTasks = project.projectReferenceTasks.map(_.fromInternal),
        name = project.name,
        description = project.description,
        ownerId = project.ownerId.fromInternal,
        flatIfSingleTask = project.flatIfSingleTask,
        readAccessors = accessorsFromInternal(project.readAccessors),
        writeAccessors = accessorsFromInternal(project.writeAccessors)
      )

  }

  implicit val projectObjectType: ObjectType[Unit, Project] = deriveObjectType[Unit, Project]()

}
