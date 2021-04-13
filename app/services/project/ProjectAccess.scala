package services.project

case class ProjectAccess[AK](accessors: Accessors)

object ProjectAccess {

  def ownerOnly[AccessK]: ProjectAccess[AccessK] =
    new ProjectAccess[AccessK](
      Accessors.Nobody
    ) {}

  def fromDb[AccessK, DBAccessK, DBAccessEntry](
      dbComponents: Option[DbComponents[DBAccessK, DBAccessEntry]]
  )(implicit accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]): ProjectAccess[AccessK] = {
    val accessors = dbComponents match {
      case Some(parameters) =>
        if (parameters.accessEntries.isEmpty)
          Accessors.Nobody
        else Accessors.Restricted(parameters.accessEntries.map(accessFromDB.entryUserId).toSet)
      case None => Accessors.Everyone
    }
    new ProjectAccess[AccessK](
      accessors = accessors
    ) {}
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
        accessConstructor: AccessToDB[AccessK, DBAccessK, DBAccessEntry]
    ): Option[DbComponents[DBAccessK, DBAccessEntry]] = {
      lazy val access = accessConstructor.mkAccess(projectId)
      projectAccess.accessors match {
        case Accessors.Everyone => None
        case Accessors.Nobody =>
          Some(
            DbComponentsImpl(
              access = access,
              accessEntries = Seq.empty
            )
          )
        case Accessors.Restricted(users) =>
          Some(
            DbComponentsImpl(
              access = access,
              accessEntries = users
                .map(userId =>
                  accessConstructor.mkAccessEntry(
                    projectId = projectId,
                    userId = userId
                  )
                )
                .toVector
            )
          )
      }

    }

  }

}
