package services.project

import db.keys.{ ProjectId, UserId }
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }

sealed trait AccessToDB[AccessK, DBAccessK, DBAccessEntry] {

  def mkAccess(projectId: ProjectId): DBAccessK
  def mkAccessEntry(projectId: ProjectId, userId: UserId, hasAccess: Boolean): DBAccessEntry

}

object AccessToDB {

  trait Instances {

    implicit val projectReadAccessToDB: AccessToDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] =
      new AccessToDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] {

        override def mkAccess(projectId: ProjectId): ProjectReadAccess =
          ProjectReadAccess(
            projectId = projectId.uuid
          )

        override def mkAccessEntry(projectId: ProjectId, userId: UserId, hasAccess: Boolean): ProjectReadAccessEntry =
          ProjectReadAccessEntry(
            projectReadAccessId = projectId.uuid,
            userId = userId.uuid,
            hasAccess = hasAccess
          )

      }

    implicit val projectWriteAccessToDB: AccessToDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] =
      new AccessToDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] {

        override def mkAccess(projectId: ProjectId): ProjectWriteAccess =
          ProjectWriteAccess(
            projectId = projectId.uuid
          )

        override def mkAccessEntry(projectId: ProjectId, userId: UserId, hasAccess: Boolean): ProjectWriteAccessEntry =
          ProjectWriteAccessEntry(
            projectWriteAccessId = projectId.uuid,
            userId = userId.uuid,
            hasAccess = hasAccess
          )

      }

  }

  object instances extends Instances
}
