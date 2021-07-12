package services.project

import db.keys.{ ProjectReadAccessId, ProjectWriteAccessId }

import java.util.UUID

case class ProjectId(uuid: UUID) extends AnyVal {
  def asProjectReadAccessId: ProjectReadAccessId = ProjectReadAccessId(uuid)
  def asProjectWriteAccessId: ProjectWriteAccessId = ProjectWriteAccessId(uuid)
}

object ProjectId {

  def fromDb(projectId: db.keys.ProjectId): ProjectId =
    ProjectId(
      uuid = projectId.uuid
    )

  def toDb(projectId: ProjectId): db.keys.ProjectId =
    db.keys.ProjectId(
      uuid = projectId.uuid
    )

}
