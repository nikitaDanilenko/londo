package services.project

import services.user.UserId

sealed trait ProjectAccess[AK] {
  def projectId: ProjectId
  def additionalUserIds: Set[UserId]
}

object ProjectAccess {

  private case class ProjectAccessImpl[AccessK](
      override val projectId: ProjectId,
      override val additionalUserIds: Set[UserId]
  ) extends ProjectAccess[AccessK]

  def ownerOnly[AccessK](projectId: ProjectId): ProjectAccess[AccessK] =
    ProjectAccessImpl(
      projectId = projectId,
      additionalUserIds = Set.empty
    )

  def fromDb[AccessK, DBAccessK, DBAccessEntry](
      access: DBAccessK,
      accessEntries: Seq[DBAccessEntry]
  )(implicit accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]): ProjectAccess[AccessK] =
    ProjectAccessImpl(
      projectId = accessFromDB.projectId(access),
      additionalUserIds = accessFromDB.entryUserIds(access, accessEntries)
    )

  def toDb[AccessK, DBAccessK, DBAccessEntry](readAccess: ProjectAccess[AccessK])(implicit
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
  ): DbComponents[DBAccessK, DBAccessEntry] =
    DbComponents(readAccess)

  sealed trait DbComponents[A, AE] {
    def access: A
    def accessEntries: Seq[AE]
  }

  object DbComponents {

    private case class DbComponentsImpl[DBAccessK, DBAccessEntry](
        override val access: DBAccessK,
        override val accessEntries: Seq[DBAccessEntry]
    ) extends DbComponents[DBAccessK, DBAccessEntry]

    def apply[AccessK, DBAccessK, DBAccessEntry](readAccess: ProjectAccess[AccessK])(implicit
        accessConstructor: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
    ): DbComponents[DBAccessK, DBAccessEntry] =
      DbComponentsImpl(
        access = accessConstructor.mkAccess(readAccess.projectId),
        accessEntries = readAccess.additionalUserIds
          .map(userId =>
            accessConstructor.mkAccessEntry(
              projectId = readAccess.projectId,
              userId = userId
            )
          )
          .toVector
      )

  }

}
