package services.project

import cats.effect.IO
import io.circe.generic.JsonCodec
import services.user.UserId
import utils.random.RandomGenerator

@JsonCodec
case class ProjectCreation(
    name: String,
    description: Option[String]
)

object ProjectCreation {

  def create(ownerId: UserId, projectCreation: ProjectCreation): IO[Project] =
    RandomGenerator.randomUUID.map { uuid =>
      Project(
        id = ProjectId(uuid),
        tasks = Vector.empty,
        subProjects = Vector.empty,
        name = projectCreation.name,
        description = projectCreation.description,
        ownerId = ownerId
      )
    }

}
