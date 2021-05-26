package services.project

import graphql.GraphQLContext
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
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

object Project {

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

  object Representation {

    implicit val representationEncoder: Encoder[Representation] = deriveEncoder[Representation]

    implicit val outputType: ObjectType[GraphQLContext, Representation] =
      deriveObjectType[GraphQLContext, Representation]()

  }

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
