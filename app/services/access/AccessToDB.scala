package services.access

import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import services.project.ProjectId
import services.user.UserId

sealed trait AccessToDB[Id, AccessK, DBAccessK, DBAccessEntry] {

  def mkAccess(id: Id, isAllowList: Boolean): DBAccessK
  def mkAccessEntry(id: Id, userId: UserId): DBAccessEntry

}

object AccessToDB {

  trait Instances {

    implicit val projectReadAccessToDB
        : AccessToDB[ProjectId, AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] =
      new AccessToDB[ProjectId, AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] {

        override def mkAccess(id: ProjectId, isAllowList: Boolean): ProjectReadAccess =
          ProjectReadAccess(
            projectId = id.uuid,
            isAllowList = isAllowList
          )

        override def mkAccessEntry(id: ProjectId, userId: UserId): ProjectReadAccessEntry =
          ProjectReadAccessEntry(
            projectReadAccessId = id.uuid,
            userId = userId.uuid
          )

      }

    implicit val projectWriteAccessToDB
        : AccessToDB[ProjectId, AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] =
      new AccessToDB[ProjectId, AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] {

        override def mkAccess(id: ProjectId, isAllowList: Boolean): ProjectWriteAccess =
          ProjectWriteAccess(
            projectId = id.uuid,
            isAllowList = isAllowList
          )

        override def mkAccessEntry(id: ProjectId, userId: UserId): ProjectWriteAccessEntry =
          ProjectWriteAccessEntry(
            projectWriteAccessId = id.uuid,
            userId = userId.uuid
          )

      }

  }

  object instances extends Instances
}
