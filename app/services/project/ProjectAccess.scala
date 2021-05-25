package services.project

import cats.data.NonEmptyList
import db.keys.ProjectId

case class ProjectAccess[AK](accessors: Accessors)

object ProjectAccess {

  def fromDb[AccessK, DBAccessK, DBAccessEntry](
      dbComponents: DbRepresentation[DBAccessK, DBAccessEntry]
  )(implicit accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]): ProjectAccess[AccessK] =
    ProjectAccess[AccessK](
      accessors = Accessors.fromRepresentation(
        Accessors.Representation(
          isAllowList = accessFromDB.isAllowList(dbComponents.access),
          userIds =
            NonEmptyList.fromList(accessFromDB.entryUserIds(dbComponents.access, dbComponents.accessEntries).toList)
        )
      )
    )

  def toDb[AccessK, DBAccessK, DBAccessEntry](projectId: ProjectId, readAccess: ProjectAccess[AccessK])(implicit
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
  ): DbRepresentation[DBAccessK, DBAccessEntry] =
    DbRepresentation(
      projectId = projectId,
      projectAccess = readAccess
    )

  sealed trait DbRepresentation[DBAccessK, DBAccessEntry] {
    def access: DBAccessK
    def accessEntries: Seq[DBAccessEntry]
  }

  object DbRepresentation {

    private case class DbRepresentationImpl[DBAccessK, DBAccessEntry](
        override val access: DBAccessK,
        override val accessEntries: Seq[DBAccessEntry]
    ) extends DbRepresentation[DBAccessK, DBAccessEntry]

    def apply[AccessK, DBAccessK, DBAccessEntry](projectId: ProjectId, projectAccess: ProjectAccess[AccessK])(implicit
        accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
    ): DbRepresentation[DBAccessK, DBAccessEntry] = {
      val userRestriction = Accessors.toRepresentation(projectAccess.accessors)
      DbRepresentationImpl(
        accessToDB.mkAccess(projectId, userRestriction.isAllowList),
        accessEntries =
          userRestriction.userIds.fold(Seq.empty[DBAccessEntry])(_.toList.map(accessToDB.mkAccessEntry(projectId, _)))
      )
    }

    def fromComponents[DBAccessK, DBAccessEntry](access: DBAccessK, accessEntries: Seq[DBAccessEntry])(implicit
        accessFromDB: AccessFromDB[_, DBAccessK, DBAccessEntry]
    ): DbRepresentation[DBAccessK, DBAccessEntry] =
      DbRepresentationImpl(
        access = access,
        accessEntries = accessFromDB.onMatchingEntries(identity)(access, accessEntries)
      )

  }

}
