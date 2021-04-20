package services.project

import cats.effect.IO
import db.keys
import db.keys.{ ProjectId, UserId }
import io.circe.generic.JsonCodec
import utils.random.RandomGenerator

@JsonCodec
case class ProjectCreation(
    ownerId: UserId,
    name: String,
    description: Option[String],
    parentProject: Option[ProjectId],
    weight: Int,
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object ProjectCreation {

  def create(projectCreation: ProjectCreation): IO[Project] =
    RandomGenerator.randomUUID.map { uuid =>
      Project(
        id = keys.ProjectId(uuid),
        tasks = Vector.empty,
        name = projectCreation.name,
        description = projectCreation.description,
        ownerId = projectCreation.ownerId,
        flatIfSingleTask = projectCreation.flatIfSingleTask,
        parentProjectId = projectCreation.parentProject,
        readAccessors = ProjectAccess[AccessKind.Read](projectCreation.readAccessors),
        writeAccessors = ProjectAccess[AccessKind.Write](projectCreation.writeAccessors)
      )
    }

}
