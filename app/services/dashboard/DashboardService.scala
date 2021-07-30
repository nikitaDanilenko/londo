package services.dashboard

import cats.data.{ EitherT, NonEmptySet }
import cats.effect.{ Async, ContextShift }
import cats.syntax.traverse._
import db.generated.daos._
import db.keys.DashboardProjectAssociationId
import db.models.{ DashboardReadAccess, DashboardReadAccessEntry, DashboardWriteAccess, DashboardWriteAccessEntry }
import db.{ DAOFunctions, Transactionally }
import doobie.ConnectionIO
import errors.{ ErrorContext, ServerError }
import monocle.syntax.all._
import services.access.AccessFromDB._
import services.access.AccessToDB._
import services.access._
import services.project.{ ProjectId, ProjectService, WeightedProject }
import services.user.UserId
import spire.math.Natural
import utils.math.NaturalUtil

import javax.inject.Inject

class DashboardService @Inject() (
    dashboardDAO: DashboardDAO,
    dashboardProjectAssociationDAO: DashboardProjectAssociationDAO,
    dashboardReadAccessDAO: DashboardReadAccessDAO,
    dashboardReadAccessEntryDAO: DashboardReadAccessEntryDAO,
    dashboardWriteAccessDAO: DashboardWriteAccessDAO,
    dashboardWriteAccessEntryDAO: DashboardWriteAccessEntryDAO,
    projectService: ProjectService,
    transactionally: Transactionally
) {

  def create[F[_]: Async: ContextShift](
      userId: UserId,
      dashboardCreation: DashboardCreation
  ): F[ServerError.Or[Dashboard]] =
    transactionally(createC(userId, dashboardCreation))

  def createC(userId: UserId, dashboardCreation: DashboardCreation): ConnectionIO[ServerError.Or[Dashboard]] =
    for {
      createdDashboard <- Async[ConnectionIO].liftIO(DashboardCreation.create(userId, dashboardCreation))
      _ <- dashboardDAO.insertC(DashboardService.toDbRepresentation(createdDashboard).dashboard)
      _ <- setReadAccessC(createdDashboard.id, createdDashboard.readAccessors)
      _ <- setWriteAccessC(createdDashboard.id, createdDashboard.writeAccessors)
      dashboard <- fetchC(createdDashboard.id)
    } yield dashboard

  def delete[F[_]: Async: ContextShift](dashboardId: DashboardId): F[ServerError.Or[Dashboard]] =
    transactionally(deleteC(dashboardId))

  def deleteC(dashboardId: DashboardId): ConnectionIO[ServerError.Or[Dashboard]] = {
    val transformer = for {
      dashboard <- fetchT(dashboardId)
      _ <- EitherT(dashboardDAO.deleteC(DashboardId.toDb(dashboardId))).leftMap(_ =>
        ErrorContext.Dashboard.Delete.asServerError
      )
    } yield dashboard

    transformer.value
  }

  def update[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      dashboardUpdate: DashboardUpdate
  ): F[ServerError.Or[Dashboard]] =
    transactionally(updateC(dashboardId, dashboardUpdate))

  def updateC(
      dashboardId: DashboardId,
      dashboardUpdate: DashboardUpdate
  ): ConnectionIO[ServerError.Or[Dashboard]] = {
    val transformer = for {
      dashboard <- fetchT(dashboardId)
      updatedDashboard = DashboardUpdate.applyToDashboard(dashboard, dashboardUpdate)
      updatedRow = DashboardService.toDbRepresentation(updatedDashboard).dashboard
      _ <- EitherT(dashboardDAO.replaceC(updatedRow)).leftMap(_ => ErrorContext.Dashboard.Replace.asServerError)
      updatedWrittenDashboard <- fetchT(dashboardId)
    } yield updatedWrittenDashboard
    transformer.value
  }

  def fetch[F[_]: Async: ContextShift](dashboardId: DashboardId): F[ServerError.Or[Dashboard]] =
    transactionally(fetchC(dashboardId))

  def fetchC(dashboardId: DashboardId): ConnectionIO[ServerError.Or[Dashboard]] = {
    val dashboardReadAccessId = dashboardId.asDashboardReadAccessId
    val dashboardWriteAccessId = dashboardId.asDashboardWriteAccessId

    val transformer = for {
      dashboardRow <- EitherT.fromOptionF(
        dashboardDAO.findC(DashboardId.toDb(dashboardId)),
        ErrorContext.Dashboard.NotFound.asServerError
      )
      dashboardProjectAssociations <-
        ServerError.liftC(dashboardProjectAssociationDAO.findByDashboardIdC(dashboardId.uuid))
      projects <- dashboardProjectAssociations.traverse(dpa =>
        EitherT(projectService.resolvedProjectC(ProjectId(dpa.projectId)))
          .subflatMap(rp =>
            NaturalUtil
              .fromInt(dpa.weight)
              .map(weight =>
                WeightedProject(
                  resolvedProject = rp,
                  weight = weight
                )
              )
          )
      )
      readAccess <- EitherT.fromOptionF(
        dashboardReadAccessDAO.findC(dashboardReadAccessId),
        ErrorContext.Dashboard.NoReadAccess.asServerError
      )
      readAccessEntries <-
        ServerError.liftC(dashboardReadAccessEntryDAO.findByDashboardReadAccessIdC(dashboardReadAccessId.uuid))
      writeAccess <- EitherT.fromOptionF(
        dashboardWriteAccessDAO.findC(dashboardWriteAccessId),
        ErrorContext.Dashboard.NoWriteAccess.asServerError
      )
      writeAccessEntries <-
        ServerError.liftC(dashboardWriteAccessEntryDAO.findByDashboardWriteAccessIdC(dashboardWriteAccessId.uuid))
    } yield Dashboard(
      id = dashboardId,
      projects = projects.toVector,
      header = dashboardRow.header,
      description = dashboardRow.description,
      userId = UserId(dashboardRow.userId),
      readAccessors = Access.fromDb(
        Access.DbRepresentation.fromComponents(
          readAccess,
          readAccessEntries
        )
      ),
      writeAccessors = Access.fromDb(
        Access.DbRepresentation.fromComponents(
          writeAccess,
          writeAccessEntries
        )
      )
    )

    transformer.value
  }

  def addProject[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      projectId: ProjectId,
      weight: Natural
  ): F[ServerError.Or[Dashboard]] =
    transactionally(addProjectC(dashboardId, projectId, weight))

  def addProjectC(
      dashboardId: DashboardId,
      projectId: ProjectId,
      weight: Natural
  ): ConnectionIO[ServerError.Or[Dashboard]] = {
    for {
      _ <- dashboardProjectAssociationDAO.insertC(
        // TODO: It seems odd that we use the uuids here directly, whereas for deletion we use typed db keys
        db.models.DashboardProjectAssociation(
          dashboardId = dashboardId.uuid,
          projectId = projectId.uuid,
          weight = weight.intValue
        )
      )
      dashboard <- fetchC(dashboardId)
    } yield dashboard
  }

  def removeProject[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      projectId: ProjectId
  ): F[ServerError.Or[Dashboard]] =
    transactionally(removeProjectC(dashboardId, projectId))

  def removeProjectC(dashboardId: DashboardId, projectId: ProjectId): ConnectionIO[ServerError.Or[Dashboard]] =
    for {
      _ <- dashboardProjectAssociationDAO.deleteC(
        DashboardProjectAssociationId(
          dashboardId = DashboardId.toDb(dashboardId),
          projectId = ProjectId.toDb(projectId)
        )
      )
      dashboard <- fetchC(dashboardId)
    } yield dashboard

  def setWeight[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      projectWeightOnDashboard: ProjectWeightOnDashboard
  ): F[ServerError.Or[Dashboard]] =
    transactionally(setWeightC(dashboardId, projectWeightOnDashboard))

  def setWeightC(
      dashboardId: DashboardId,
      projectWeightOnDashboard: ProjectWeightOnDashboard
  ): ConnectionIO[ServerError.Or[Dashboard]] =
    for {
      _ <- dashboardProjectAssociationDAO.replaceC(
        ProjectWeightOnDashboard.toDb(dashboardId, projectWeightOnDashboard)
      )
      dashboard <- fetchC(dashboardId)
    } yield dashboard

  def setWeights[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      projectWeights: Seq[ProjectWeightOnDashboard]
  ): F[ServerError.Or[Dashboard]] =
    transactionally(setWeightsC(dashboardId, projectWeights))

  def setWeightsC(
      dashboardId: DashboardId,
      projectWeights: Seq[ProjectWeightOnDashboard]
  ): ConnectionIO[ServerError.Or[Dashboard]] =
    projectWeights.traverse(setWeightC(dashboardId, _)).flatMap(_ => fetchC(dashboardId))

  def allowReadUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Or[Accessors]] =
    transactionally(toAccessors(allowReadUsersC(dashboardId, userIds)))

  def allowReadUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.readAccessors, Accessors.allowUsers, setReadAccessC)

  def allowWriteUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Or[Accessors]] =
    transactionally(toAccessors(allowWriteUsersC(dashboardId, userIds)))

  def allowWriteUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.writeAccessors, Accessors.allowUsers, setWriteAccessC)

  def blockReadUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Or[Accessors]] =
    transactionally(toAccessors(blockReadUsersC(dashboardId, userIds)))

  def blockReadUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.readAccessors, Accessors.blockUsers, setReadAccessC)

  def blockWriteUsers[F[_]: Async: ContextShift](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Or[Accessors]] =
    transactionally(toAccessors(blockWriteUsersC(dashboardId, userIds)))

  def blockWriteUsersC(
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]]] =
    modifyUsersWithRights(dashboardId, userIds, _.writeAccessors, Accessors.blockUsers, setWriteAccessC)

  private def modifyUsersWithRights[AK, DBAccessK, DBAccessEntry](
      dashboardId: DashboardId,
      userIds: NonEmptySet[UserId],
      accessors: Dashboard => Access[AK],
      modifier: (Accessors, NonEmptySet[UserId]) => Accessors,
      setAccess: (
          DashboardId,
          Access[AK]
      ) => ConnectionIO[ServerError.Or[Access.DbRepresentation[DBAccessK, DBAccessEntry]]]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DBAccessK, DBAccessEntry]]] = {
    val transformer =
      for {
        dashboard <- fetchT(dashboardId)
        updatedAccess <- EitherT(
          setAccess(
            dashboardId,
            accessors(dashboard)
              .focus(_.accessors)
              .modify(modifier(_, userIds))
          )
        )
      } yield updatedAccess

    transformer.value
  }

  private def fetchT(dashboardId: DashboardId): EitherT[ConnectionIO, ServerError, Dashboard] =
    EitherT(fetchC(dashboardId))

  private def setReadAccessC(
      dashboardId: DashboardId,
      dashboardAccess: Access[AccessKind.Read]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardReadAccess, DashboardReadAccessEntry]]] =
    setAccess(dashboardReadAccessDAO, dashboardReadAccessEntryDAO)(dashboardId, dashboardAccess)

  private def setWriteAccessC(
      dashboardId: DashboardId,
      dashboardAccess: Access[AccessKind.Write]
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DashboardWriteAccess, DashboardWriteAccessEntry]]] =
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
  ): ConnectionIO[ServerError.Or[Access.DbRepresentation[DBAccessK, DBAccessEntry]]] = {
    val components = Access.DbRepresentation(dashboardId, dashboardAccess)
    val transformer = for {
      access <- EitherT(daoFunctionsDBAccessK.insertC(components.access)).leftMap(_ =>
        ErrorContext.Dashboard.AccessDbError.asServerError
      )
      entries <- EitherT(daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)).leftMap(_ =>
        ErrorContext.Dashboard.AccessEntryDbError.asServerError
      )
    } yield {
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
    transformer.value
  }

  private def toAccessors[AccessK, DBAccessK, DBAccessEntry](
      dbComponentsC: ConnectionIO[ServerError.Or[Access.DbRepresentation[DBAccessK, DBAccessEntry]]]
  )(implicit
      accessFromDB: AccessFromDB[DashboardId, AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[ServerError.Or[Accessors]] = dbComponentsC.map(_.map(Access.fromDb(_).accessors))

}

object DashboardService {

  def toDbRepresentation(dashboard: Dashboard): DbRepresentation =
    DbRepresentation(dashboard)

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
        dashboardProjectAssociations = dashboard.projects.map(wp =>
          db.models.DashboardProjectAssociation(
            dashboardId = dashboard.id.uuid,
            projectId = wp.resolvedProject.id.uuid,
            weight = wp.weight.intValue
          )
        )
      )
    }

  }

}
