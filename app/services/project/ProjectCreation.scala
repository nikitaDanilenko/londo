package services.project

import cats.effect.IO
import services.user.UserId
import utils.random.RandomGenerator

case class ProjectCreation(
    name: String,
    description: Option[String],
    parentProject: Option[ProjectId],
    flatIfSingleTask: Boolean,
    readAccessors: Accessors.Representation,
    writeAccessors: Accessors.Representation
)

object ProjectCreation {

  def create(ownerId: UserId, projectCreation: ProjectCreation): IO[Project] =
    RandomGenerator.randomUUID.map { uuid =>
      Project(
        id = ProjectId(uuid),
        plainTasks = Vector.empty,
        projectReferenceTasks = Vector.empty,
        name = projectCreation.name,
        description = projectCreation.description,
        ownerId = ownerId,
        flatIfSingleTask = projectCreation.flatIfSingleTask,
        parentProjectId = projectCreation.parentProject,
        readAccessors = ProjectAccess[AccessKind.Read](Accessors.fromRepresentation(projectCreation.readAccessors)),
        writeAccessors = ProjectAccess[AccessKind.Write](Accessors.fromRepresentation(projectCreation.writeAccessors))
      )
    }

}
