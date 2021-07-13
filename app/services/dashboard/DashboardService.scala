package services.dashboard

import cats.data.{ EitherT, NonEmptyList, NonEmptySet }
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import db.generated.daos._
import db.models.{ DashboardReadAccess, DashboardReadAccessEntry, DashboardWriteAccess, DashboardWriteAccessEntry }
import db.{ DAOFunctions, Transactionally }
import doobie.ConnectionIO
import errors.ServerError
import monocle.syntax.all._
import services.access._
import services.user.UserId
import AccessToDB._
import AccessFromDB._

import javax.inject.Inject

class DashboardService @Inject() (
    dashboardDAO: DashboardDAO,
    dashboardProjectAssociationDAO: DashboardProjectAssociationDAO,
    dashboardReadAccessDAO: DashboardReadAccessDAO,
    dashboardReadAccessEntryDAO: DashboardReadAccessEntryDAO,
    dashboardWriteAccessDAO: DashboardWriteAccessDAO,
    dashboardWriteAccessEntryDAO: DashboardWriteAccessEntryDAO,
    transactionally: Transactionally
) {

  private def setReadAccessC(
      dashboardId: DashboardId,
      dashboardAccess: Access[AccessKind.Read]
  ): ConnectionIO[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]] =
    setAccess(dashboardReadAccessDAO, dashboardReadAccessEntryDAO)(dashboardId, dashboardAccess)

  private def setWriteAccessC(
      dashboardId: DashboardId,
      dashboardAccess: Access[AccessKind.Write]
  ): ConnectionIO[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]] =
    setAccess(dashboardWriteAccessDAO, dashboardWriteAccessEntryDAO)(dashboardId, dashboardAccess)

  private def setAccess[AccessK, DBAccessK, DBAccessKey, DBAccessEntry, DBAccessEntryKey](
      daoFunctionsDBAccessK: DAOFunctions[DBAccessK, DBAccessKey],
      daoFunctionsDBAccessEntry: DAOFunctions[DBAccessEntry, DBAccessEntryKey]
  )(
      dashboardId: DashboardId,
      dashboardAccess: Access[AccessK]
  )(implicit
      accessToDB: AccessToDB[DashboardId, AccessK, DBAccessK, DBAccessEntry],
      accessFromDB: AccessFromDB[DashboardId, AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[Access.DbRepresentation[DBAccessK, DBAccessEntry]] = {
    val components = Access.DbRepresentation(dashboardId, dashboardAccess)
    (
      daoFunctionsDBAccessK.insertC(components.access),
      daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)
    ).mapN { (access, entries) =>
      Access.DbRepresentation[DashboardId, AccessK, DBAccessK, DBAccessEntry](
        id = accessFromDB.id(access),
        access = Access.fromDb(
          Access.DbRepresentation.fromComponents(
            access = access,
            accessEntries = entries
          )
        )
      )
    }
  }

  def fetch[F[_]: Async: ContextShift](dashboardId: DashboardId): F[ServerError.Valid[Dashboard]] =
    transactionally(fetchC(dashboardId))

  def fetchC(dashboardId: DashboardId): ConnectionIO[ServerError.Valid[Dashboard]] = {
    ???
  }

  private def toAccessors[AccessK, DBAccessK, DBAccessEntry](
      dbComponentsC: ConnectionIO[ServerError.Valid[Access.DbRepresentation[DBAccessK, DBAccessEntry]]]
  )(implicit
      accessFromDB: AccessFromDB[DashboardId, AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[ServerError.Valid[Accessors]] = dbComponentsC.map(_.map(Access.fromDb(_).accessors))

  def allowReadUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(allowReadUsersC(dashboardId, userIds)))

  def allowReadUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.readAccessors, Accessors.allowUsers, setReadAccessC)

  def allowWriteUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(allowWriteUsersC(dashboardId, userIds)))

  def allowWriteUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.writeAccessors, Accessors.allowUsers, setWriteAccessC)

  def blockReadUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(blockReadUsersC(dashboardId, userIds)))

  def blockReadUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.readAccessors, Accessors.blockUsers, setReadAccessC)

  def blockWriteUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(blockWriteUsersC(dashboardId, userIds)))

  def blockWriteUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.writeAccessors, Accessors.blockUsers, setWriteAccessC)

  private def modifyUsersWithRights[AK, DBAccessK, DBAccessEntry](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId],
      accessors: Dashboard => Access[AK],
      modifier: (Accessors, NonEmptySet[UserId]) => Accessors,
      setAccess: (
          DashboardId,
          Access[AK]
      ) => ConnectionIO[Access.DbRepresentation[DBAccessK, DBAccessEntry]]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DBAccessK, DBAccessEntry]]] = {
    val transformer =
      for {
        dashboard <- fetchT(dashboardId)
        updatedAccess <- ServerError.liftC(
          setAccess(
            dashboardId,
            accessors(dashboard)
              .focus(_.accessors)
              .modify(modifier(_, userIds))
          )
        )
      } yield updatedAccess

    transformer.value.map(ServerError.fromEitherNel)
  }

  private def fetchT(dashboardId: DashboardId): EitherT[ConnectionIO, NonEmptyList[ServerError], Dashboard] =
    EitherT(fetchC(dashboardId).map(_.toEither))

}

object DashboardService {

  def toDbRepresentation(dashboard: Dashboard): DbRepresentation =
    DbRepresentation(dashboard)

  def fromDbRepresentation(
      dbComponents: DbRepresentation
  ): ServerError.Valid[Dashboard] = ???

//    Dashboard(
//      id = DashboardId(dbComponents.dashboard.id),
//      header = dbComponents.dashboard.header,
//      description = dbComponents.dashboard.description,
//      userId = UserId(dbComponents.dashboard.userId),
//      readAccessors = Access.fromDb(dbComponents.readAccess),
//      writeAccessors = Access.fromDb(dbComponents.writeAccess)
//    )

  sealed trait DbRepresentation {
    def dashboard: db.models.Dashboard
    def readAccess: Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]
    def writeAccess: Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]
    def dashboardProjectAssociations: Seq[db.models.DashboardProjectAssociation]
  }

  object DbRepresentation {

    private[DashboardService] case class Impl(
        override val dashboard: db.models.Dashboard,
        override val readAccess: Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry],
        override val writeAccess: Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry],
        override val dashboardProjectAssociations: Seq[db.models.DashboardProjectAssociation]
    ) extends DbRepresentation

    def apply(dashboard: Dashboard): DbRepresentation = {
      Impl(
        dashboard = db.models.Dashboard(
          id = dashboard.id.uuid,
          userId = dashboard.id.uuid,
          header = dashboard.header,
          description = dashboard.description
        ),
        readAccess = Access.toDb(dashboard.id, dashboard.readAccessors),
        writeAccess = Access.toDb(dashboard.id, dashboard.writeAccessors),
        dashboardProjectAssociations = ???
      )
    }

  }

}
