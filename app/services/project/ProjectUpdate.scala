package services.project

import db.keys.{ ProjectId, UserId }
import io.circe.generic.JsonCodec

@JsonCodec
case class ProjectUpdate(
    name: String,
    description: Option[String],
    ownerId: UserId,
    parentProjectId: Option[ProjectId],
    flatIfSingleTask: Boolean
)

object ProjectUpdate {

  def applyToProject(project: Project, projectUpdate: ProjectUpdate): Project =
    project.copy(
      name = projectUpdate.name,
      description = projectUpdate.description,
      ownerId = projectUpdate.ownerId,
      parentProjectId = projectUpdate.parentProjectId,
      flatIfSingleTask = projectUpdate.flatIfSingleTask
    )

}
