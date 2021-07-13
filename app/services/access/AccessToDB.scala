package services.access

import db.models._
import services.dashboard.DashboardId
import services.project.ProjectId
import services.user.UserId

sealed trait AccessToDB[Id, AccessK, DBAccessK, DBAccessEntry] {

  def mkAccess(id: Id, isAllowList: Boolean): DBAccessK
  def mkAccessEntry(id: Id, userId: UserId): DBAccessEntry

}

object AccessToDB {

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

  implicit val dashboardReadAccessToDB
      : AccessToDB[DashboardId, AccessKind.Read, DashboardReadAccess, DashboardReadAccessEntry] =
    new AccessToDB[DashboardId, AccessKind.Read, DashboardReadAccess, DashboardReadAccessEntry] {

      override def mkAccess(id: DashboardId, isAllowList: Boolean): DashboardReadAccess =
        DashboardReadAccess(
          dashboardId = id.uuid,
          isAllowList = isAllowList
        )

      override def mkAccessEntry(id: DashboardId, userId: UserId): DashboardReadAccessEntry =
        DashboardReadAccessEntry(
          dashboardReadAccessId = id.uuid,
          userId = userId.uuid
        )

    }

  implicit val dashboardWriteAccessToDB
      : AccessToDB[DashboardId, AccessKind.Write, DashboardWriteAccess, DashboardWriteAccessEntry] =
    new AccessToDB[DashboardId, AccessKind.Write, DashboardWriteAccess, DashboardWriteAccessEntry] {

      override def mkAccess(id: DashboardId, isAllowList: Boolean): DashboardWriteAccess =
        DashboardWriteAccess(
          dashboardId = id.uuid,
          isAllowList = isAllowList
        )

      override def mkAccessEntry(id: DashboardId, userId: UserId): DashboardWriteAccessEntry =
        DashboardWriteAccessEntry(
          dashboardWriteAccessId = id.uuid,
          userId = userId.uuid
        )

    }

}
