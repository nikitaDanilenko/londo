package services.project

import services.user.UserId

case class Project(
    id: ProjectId,
    tasks: Vector[Task],
    subProjects: Vector[Project],
    name: String,
    description: Option[String],
    ownerId: UserId
)
