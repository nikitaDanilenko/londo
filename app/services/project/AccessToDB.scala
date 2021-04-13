package services.project

import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import services.user.UserId

sealed trait AccessToDB[AccessK, DBAccessK, DBAccessEntry] {

  def mkAccess(projectId: ProjectId): DBAccessK
  def mkAccessEntry(projectId: ProjectId, userId: UserId): DBAccessEntry

}

object AccessToDB {

  trait Instances {

    implicit val projectReadAccessToDB: AccessToDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] =
      new AccessToDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] {

        override def mkAccess(projectId: ProjectId): ProjectReadAccess =
          ProjectReadAccess(
            projectId = projectId.uuid
          )

        override def mkAccessEntry(projectId: ProjectId, userId: UserId): ProjectReadAccessEntry =
          ProjectReadAccessEntry(
            projectReadAccessId = projectId.uuid,
            userId = userId.uuid
          )

      }

    implicit val projectWriteAccessToDB: AccessToDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] =
      new AccessToDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] {

        override def mkAccess(projectId: ProjectId): ProjectWriteAccess =
          ProjectWriteAccess(
            projectId = projectId.uuid
          )

        override def mkAccessEntry(projectId: ProjectId, userId: UserId): ProjectWriteAccessEntry =
          ProjectWriteAccessEntry(
            projectWriteAccessId = projectId.uuid,
            userId = userId.uuid
          )

      }

  }

  object instances extends Instances
}
