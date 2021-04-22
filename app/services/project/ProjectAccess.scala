package services.project

import db.keys.ProjectId

case class ProjectAccess[AK](accessors: Accessors)

object ProjectAccess {

  def ownerOnly[AccessK]: ProjectAccess[AccessK] =
    ProjectAccess[AccessK](
      Accessors.Nobody
    )

  def fromDb[AccessK, DBAccessK, DBAccessEntry](
      dbComponents: Option[DbComponents[DBAccessK, DBAccessEntry]]
  )(implicit accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]): ProjectAccess[AccessK] = {
    ProjectAccess[AccessK](
      accessors = Accessors.fromRepresentation(dbComponents.map(_.accessEntries.map(accessFromDB.entryUserId)))
    )
  }

  def toDb[AccessK, DBAccessK, DBAccessEntry](projectId: ProjectId, readAccess: ProjectAccess[AccessK])(implicit
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
  ): Option[DbComponents[DBAccessK, DBAccessEntry]] =
    DbComponents(
      projectId = projectId,
      projectAccess = readAccess
    )

  sealed trait DbComponents[DBAccessK, DBAccessEntry] {
    def access: DBAccessK
    def accessEntries: Seq[DBAccessEntry]
  }

  object DbComponents {

    private case class DbComponentsImpl[DBAccessK, DBAccessEntry](
        override val access: DBAccessK,
        override val accessEntries: Seq[DBAccessEntry]
    ) extends DbComponents[DBAccessK, DBAccessEntry]

    def apply[AccessK, DBAccessK, DBAccessEntry](projectId: ProjectId, projectAccess: ProjectAccess[AccessK])(implicit
        accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
    ): Option[DbComponents[DBAccessK, DBAccessEntry]] =
      Accessors.toRepresentation(projectAccess.accessors).map { userIds =>
        DbComponentsImpl(
          accessToDB.mkAccess(projectId),
          accessEntries = userIds.map(accessToDB.mkAccessEntry(projectId, _))
        )
      }

    def fromComponents[DBAccessK, DBAccessEntry](access: DBAccessK, accessEntries: Seq[DBAccessEntry])(implicit
        accessFromDB: AccessFromDB[_, DBAccessK, DBAccessEntry]
    ): DbComponents[DBAccessK, DBAccessEntry] =
      DbComponentsImpl(
        access = access,
        accessEntries = accessFromDB.onMatchingEntries(identity)(access, accessEntries)
      )

  }

}
