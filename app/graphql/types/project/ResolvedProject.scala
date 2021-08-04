package graphql.types.project

import graphql.types.FromInternal
import graphql.types.access.Accessors
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import FromInternal.syntax._
import graphql.types.task.ResolvedTask
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class ResolvedProject(
    id: ProjectId,
    plainTasks: Vector[ResolvedTask.Plain],
    projectReferenceTasks: Vector[ResolvedTask.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object ResolvedProject {

  implicit val projectFromInternal: FromInternal[ResolvedProject, services.project.ResolvedProject] = { project =>
    ResolvedProject(
      id = project.id.fromInternal,
      plainTasks = project.plainTasks.map(_.fromInternal),
      projectReferenceTasks = project.projectReferenceTasks.map(_.fromInternal),
      name = project.name,
      description = project.description,
      ownerId = project.ownerId.fromInternal,
      flatIfSingleTask = project.flatIfSingleTask,
      readAccessors = Accessors.fromInternalAccess(project.readAccessors),
      writeAccessors = Accessors.fromInternalAccess(project.writeAccessors)
    )
  }

  implicit val projectObjectType: ObjectType[Unit, ResolvedProject] = deriveObjectType[Unit, ResolvedProject]()

}
