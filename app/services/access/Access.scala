package services.access

import cats.data.NonEmptyList

case class Access[AK](accessors: Accessors)

object Access {

  def fromDb[Id, AccessK, DBAccessK, DBAccessEntry](
      dbComponents: DbRepresentation[DBAccessK, DBAccessEntry]
  )(implicit accessFromDB: AccessFromDB[Id, AccessK, DBAccessK, DBAccessEntry]): Access[AccessK] =
    Access[AccessK](
      accessors = Accessors.fromRepresentation(
        Accessors.Representation(
          isAllowList = accessFromDB.isAllowList(dbComponents.access),
          userIds =
            NonEmptyList.fromList(accessFromDB.entryUserIds(dbComponents.access, dbComponents.accessEntries).toList)
        )
      )
    )

  def toDb[Id, AccessK, DBAccessK, DBAccessEntry](id: Id, readAccess: Access[AccessK])(implicit
      accessToDB: AccessToDB[Id, AccessK, DBAccessK, DBAccessEntry]
  ): DbRepresentation[DBAccessK, DBAccessEntry] =
    DbRepresentation(
      id = id,
      access = readAccess
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

    def apply[Id, AccessK, DBAccessK, DBAccessEntry](id: Id, access: Access[AccessK])(implicit
        accessToDB: AccessToDB[Id, AccessK, DBAccessK, DBAccessEntry]
    ): DbRepresentation[DBAccessK, DBAccessEntry] = {
      val userRestriction = Accessors.toRepresentation(access.accessors)
      DbRepresentationImpl(
        accessToDB.mkAccess(id, userRestriction.isAllowList),
        accessEntries =
          userRestriction.userIds.fold(Seq.empty[DBAccessEntry])(_.toList.map(accessToDB.mkAccessEntry(id, _)))
      )
    }

    def fromComponents[DBAccessK, DBAccessEntry](access: DBAccessK, accessEntries: Seq[DBAccessEntry])(implicit
        accessFromDB: AccessFromDB[_, _, DBAccessK, DBAccessEntry]
    ): DbRepresentation[DBAccessK, DBAccessEntry] =
      DbRepresentationImpl(
        access = access,
        accessEntries = accessFromDB.onMatchingEntries(identity)(access, accessEntries)
      )

  }

}
