package services.project

import cats.effect.IO
import io.circe.generic.JsonCodec
import services.user.UserId
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
        id = ProjectId(uuid),
        tasks = Vector.empty,
        subProjects = Vector.empty,
        name = projectCreation.name,
        description = projectCreation.description,
        ownerId = projectCreation.ownerId,
        flatIfSingleTask = projectCreation.flatIfSingleTask,
        parentProjectId = projectCreation.parentProject,
        readAccessors = projectCreation.readAccessors,
        writeAccessors = projectCreation.writeAccessors
      )
    }

}
