package services.project

import services.user.UserId

case class Project(
    id: ProjectId,
    tasks: Vector[Task],
    subProjects: Vector[Project],
    name: String,
    description: Option[String],
    ownerId: UserId,
    parentProjectId: Option[ProjectId],
    weight: Int,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object Project {

  def toRow(project: Project): DbComponents =
    DbComponents(
      project = db.models.Project(
        id = project.id.uuid,
        ownerId = project.ownerId.uuid,
        name = project.name,
        description = project.description,
        parentProjectId = project.parentProjectId.map(_.uuid),
        weight = project.weight,
        isReadRestricted = Accessors.isRestricted(project.readAccessors),
        isWriteRestricted = Accessors.isRestricted(project.writeAccessors)
      ),
      accessors =
        accessorsWith(
          writeAllowed = false,
          accessors = project.readAccessors,
          projectId = project.id
        ) ++
          accessorsWith(
            writeAllowed = true,
            accessors = project.writeAccessors,
            projectId = project.id
          )
    )

  private def accessorsWith(
      writeAllowed: Boolean,
      accessors: Accessors,
      projectId: ProjectId
  ): Set[db.models.ProjectAccess] =
    Accessors
      .userIdsOf(accessors)
      .map(userId => db.models.ProjectAccess(projectId.uuid, userId.uuid, writeAllowed = writeAllowed))

  case class DbComponents(
      project: db.models.Project,
      accessors: Set[db.models.ProjectAccess]
  )

}
