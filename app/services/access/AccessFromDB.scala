package services.access

import db.models._
import services.dashboard.DashboardId
import services.project.{ ProjectId, ReadAccessId, WriteAccessId }
import services.user.UserId

sealed trait AccessFromDB[Id, AccessK, DBAccessK, DBAccessEntry] {
  type AccessId
  def id(dbAccess: DBAccessK): Id
  def accessId(dbAccess: DBAccessK): AccessId
  def entryAccessId(dbAccessEntry: DBAccessEntry): AccessId
  def entryUserId(dbAccessEntry: DBAccessEntry): UserId
  def isAllowList(dbAccess: DBAccessK): Boolean

  def entryUserIds(dbAccess: DBAccessK, dbAccessEntries: Seq[DBAccessEntry]): Seq[UserId] =
    onMatchingEntries(entryUserId)(dbAccess, dbAccessEntries)

  def onMatchingEntries[A](f: DBAccessEntry => A)(dbAccess: DBAccessK, dbAccessEntries: Seq[DBAccessEntry]): Seq[A] =
    dbAccessEntries.collect {
      case dbAccessEntry if entryAccessId(dbAccessEntry) == accessId(dbAccess) => f(dbAccessEntry)
    }

}

object AccessFromDB {

  implicit val projectReadAccessFromDB
      : AccessFromDB[ProjectId, AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] =
    new AccessFromDB[ProjectId, AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] {
      override type AccessId = ReadAccessId

      override def id(dbAccess: ProjectReadAccess): ProjectId = ProjectId(dbAccess.projectId)
      override def accessId(dbAccess: ProjectReadAccess): AccessId = ReadAccessId(dbAccess.projectId)

      override def entryAccessId(dbAccessEntry: ProjectReadAccessEntry): AccessId =
        ReadAccessId(dbAccessEntry.projectReadAccessId)

      override def entryUserId(dbAccessEntry: ProjectReadAccessEntry): UserId = UserId(dbAccessEntry.userId)
      override def isAllowList(dbAccess: ProjectReadAccess): Boolean = dbAccess.isAllowList
    }

  implicit val projectWriteAccessFromDB
      : AccessFromDB[ProjectId, AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] =
    new AccessFromDB[ProjectId, AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] {

      override type AccessId = WriteAccessId
      override def id(dbAccess: ProjectWriteAccess): ProjectId = ProjectId(dbAccess.projectId)
      override def accessId(dbAccess: ProjectWriteAccess): AccessId = WriteAccessId(dbAccess.projectId)

      override def entryAccessId(dbAccessEntry: ProjectWriteAccessEntry): AccessId =
        WriteAccessId(dbAccessEntry.projectWriteAccessId)

      override def entryUserId(dbAccessEntry: ProjectWriteAccessEntry): UserId = UserId(dbAccessEntry.userId)
      override def isAllowList(dbAccess: ProjectWriteAccess): Boolean = dbAccess.isAllowList
    }

  implicit val dashboardReadAccessFromDB
      : AccessFromDB[DashboardId, AccessKind.Read, DashboardReadAccess, DashboardReadAccessEntry] =
    new AccessFromDB[DashboardId, AccessKind.Read, DashboardReadAccess, DashboardReadAccessEntry] {
      override type AccessId = ReadAccessId

      override def id(dbAccess: DashboardReadAccess): DashboardId = DashboardId(dbAccess.dashboardId)
      override def accessId(dbAccess: DashboardReadAccess): AccessId = ReadAccessId(dbAccess.dashboardId)

      override def entryAccessId(dbAccessEntry: DashboardReadAccessEntry): AccessId =
        ReadAccessId(dbAccessEntry.dashboardReadAccessId)

      override def entryUserId(dbAccessEntry: DashboardReadAccessEntry): UserId = UserId(dbAccessEntry.userId)
      override def isAllowList(dbAccess: DashboardReadAccess): Boolean = dbAccess.isAllowList
    }

  implicit val dashboardWriteAccessFromDB
      : AccessFromDB[DashboardId, AccessKind.Write, DashboardWriteAccess, DashboardWriteAccessEntry] =
    new AccessFromDB[DashboardId, AccessKind.Write, DashboardWriteAccess, DashboardWriteAccessEntry] {

      override type AccessId = WriteAccessId
      override def id(dbAccess: DashboardWriteAccess): DashboardId = DashboardId(dbAccess.dashboardId)
      override def accessId(dbAccess: DashboardWriteAccess): AccessId = WriteAccessId(dbAccess.dashboardId)

      override def entryAccessId(dbAccessEntry: DashboardWriteAccessEntry): AccessId =
        WriteAccessId(dbAccessEntry.dashboardWriteAccessId)

      override def entryUserId(dbAccessEntry: DashboardWriteAccessEntry): UserId = UserId(dbAccessEntry.userId)
      override def isAllowList(dbAccess: DashboardWriteAccess): Boolean = dbAccess.isAllowList
    }

}
