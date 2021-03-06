package services.project

import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import services.user.UserId

sealed trait AccessFromDB[AccessK, DBAccessK, DBAccessEntry] {
  type AccessId
  def projectId(dbAccess: DBAccessK): ProjectId
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

  trait Instances {

    implicit val projectReadAccessFromDB: AccessFromDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] =
      new AccessFromDB[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry] {
        override type AccessId = ReadAccessId

        override def projectId(dbAccess: ProjectReadAccess): ProjectId = ProjectId(dbAccess.projectId)
        override def accessId(dbAccess: ProjectReadAccess): AccessId = ReadAccessId(dbAccess.projectId)

        override def entryAccessId(dbAccessEntry: ProjectReadAccessEntry): AccessId =
          ReadAccessId(dbAccessEntry.projectReadAccessId)

        override def entryUserId(dbAccessEntry: ProjectReadAccessEntry): UserId = UserId(dbAccessEntry.userId)
        override def isAllowList(dbAccess: ProjectReadAccess): Boolean = dbAccess.isAllowList
      }

    implicit val projectWriteAccessFromDB: AccessFromDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] =
      new AccessFromDB[AccessKind.Write, ProjectWriteAccess, ProjectWriteAccessEntry] {

        override type AccessId = WriteAccessId
        override def projectId(dbAccess: ProjectWriteAccess): ProjectId = ProjectId(dbAccess.projectId)
        override def accessId(dbAccess: ProjectWriteAccess): AccessId = WriteAccessId(dbAccess.projectId)

        override def entryAccessId(dbAccessEntry: ProjectWriteAccessEntry): AccessId =
          WriteAccessId(dbAccessEntry.projectWriteAccessId)

        override def entryUserId(dbAccessEntry: ProjectWriteAccessEntry): UserId = UserId(dbAccessEntry.userId)
        override def isAllowList(dbAccess: ProjectWriteAccess): Boolean = dbAccess.isAllowList
      }

  }

  object instances extends Instances
}
