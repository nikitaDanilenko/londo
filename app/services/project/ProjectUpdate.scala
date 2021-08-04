package services.project

import services.user.UserId

case class ProjectUpdate(
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean
)

object ProjectUpdate {

  def applyToProject(project: Project, projectUpdate: ProjectUpdate): Project =
    project.copy(
      name = projectUpdate.name,
      description = projectUpdate.description,
      ownerId = projectUpdate.ownerId,
      flatIfSingleTask = projectUpdate.flatIfSingleTask
    )

}
