package services.project

import cats.data.{ EitherT, NonEmptyList, NonEmptySet }
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import cats.syntax.traverse._
import db.generated.daos._
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import db.{ DAOFunctions, Transactionally }
import doobie.ConnectionIO
import errors.ServerError
import monocle.syntax.all._
import services.access._
import services.task.{ ResolvedTask, Task }
import services.user.UserId

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    projectReadAccessDAO: ProjectReadAccessDAO,
    projectReadAccessEntryDAO: ProjectReadAccessEntryDAO,
    projectWriteAccessDAO: ProjectWriteAccessDAO,
    projectWriteAccessEntryDAO: ProjectWriteAccessEntryDAO,
    plainTaskDAO: PlainTaskDAO,
    projectReferenceTaskDAO: ProjectReferenceTaskDAO,
    transactionally: Transactionally
) {

  def create[F[_]: Async: ContextShift](
      ownerId: UserId,
      projectCreation: ProjectCreation
  ): F[ServerError.Valid[Project]] =
    transactionally(createC(ownerId, projectCreation))

  def createC(ownerId: UserId, projectCreation: ProjectCreation): ConnectionIO[ServerError.Valid[Project]] =
    for {
      createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(ownerId, projectCreation))
      _ <- projectDAO.insertC(ProjectService.toDbRepresentation(createdProject).project)
      _ <- setReadAccessC(createdProject.id, createdProject.readAccessors)
      _ <- setWriteAccessC(createdProject.id, createdProject.writeAccessors)
      project <- fetchC(createdProject.id)
    } yield project

  def delete[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[Project]] =
    transactionally(deleteC(projectId))

  def deleteC(projectId: ProjectId): ConnectionIO[ServerError.Valid[Project]] = {
    val transformer = for {
      project <- fetchT(projectId)
      _ <- EitherT.liftF[ConnectionIO, NonEmptyList[ServerError], db.models.Project](
        projectDAO.deleteC(ProjectId.toDb(projectId))
      )
    } yield project

    transformer.value.map(ServerError.fromEitherNel)
  }

  def update[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectUpdate: ProjectUpdate
  ): F[ServerError.Valid[Project]] =
    transactionally(updateC(projectId, projectUpdate))

  def updateC(projectId: ProjectId, projectUpdate: ProjectUpdate): ConnectionIO[ServerError.Valid[Project]] = {
    val transformer = for {
      project <- fetchT(projectId)
      updatedProject = ProjectUpdate.applyToProject(project, projectUpdate)
      updatedRow = ProjectService.toDbRepresentation(updatedProject).project
      _ <- ServerError.liftNelC(projectDAO.replaceC(updatedRow))
      updatedWrittenProject <- fetchT(projectId)
    } yield updatedWrittenProject
    transformer.value.map(ServerError.fromEitherNel)
  }

  def fetch[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[Project]] =
    transactionally(fetchC(projectId))

  def fetchC(projectId: ProjectId): ConnectionIO[ServerError.Valid[Project]] = {
    val projectReadAccessId = projectId.asProjectReadAccessId
    val projectWriteAccessId = projectId.asProjectWriteAccessId

    val action = for {
      projectRow <- EitherT.fromOptionF(projectDAO.findC(ProjectId.toDb(projectId)), ServerError.Project.NotFound)
      plainTasks <- ServerError.liftC(plainTaskDAO.findByProjectIdC(projectId.uuid))
      projectReferenceTasks <- ServerError.liftC(projectReferenceTaskDAO.findByProjectIdC(projectId.uuid))
      readAccess <- ServerError.liftC(projectReadAccessDAO.findC(projectReadAccessId))
      readAccessEntries <-
        ServerError.liftC(projectReadAccessEntryDAO.findByProjectReadAccessIdC(projectReadAccessId.uuid))
      writeAccess <- ServerError.liftC(projectWriteAccessDAO.findC(projectWriteAccessId))
      writeAccessEntries <-
        ServerError.liftC(projectWriteAccessEntryDAO.findByProjectWriteAccessIdC(projectWriteAccessId.uuid))
    } yield {
      ProjectService.fromDbRepresentation(
        ProjectService.DbRepresentation.Impl(
          project = projectRow,
          plainTasks = plainTasks,
          projectReferenceTasks = projectReferenceTasks,
          readAccess = Access.DbRepresentation.fromComponents(
            readAccess.getOrElse(db.models.ProjectReadAccess(projectId.uuid, isAllowList = false)),
            readAccessEntries
          ),
          writeAccess = Access.DbRepresentation.fromComponents(
            writeAccess.getOrElse(db.models.ProjectWriteAccess(projectId.uuid, isAllowList = false)),
            writeAccessEntries
          )
        )
      )
    }
    action
      .fold(
        error => ServerError.fromEither[Project](Left(error)),
        identity
      )
  }

  def allowReadUsers[F[_]: Async: ContextShift](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(allowReadUsersC(projectId, userIds)))

  def allowReadUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.allowUsers, setReadAccessC)

  def allowWriteUsers[F[_]: Async: ContextShift](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(allowWriteUsersC(projectId, userIds)))

  def allowWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.allowUsers, setWriteAccessC)

  def blockReadUsers[F[_]: Async: ContextShift](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(blockReadUsersC(projectId, userIds)))

  def blockReadUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.blockUsers, setReadAccessC)

  def blockWriteUsers[F[_]: Async: ContextShift](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): F[ServerError.Valid[Accessors]] =
    transactionally(toAccessors(blockWriteUsersC(projectId, userIds)))

  def blockWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.blockUsers, setWriteAccessC)

  def resolveProject[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[ResolvedProject]] =
    transactionally(resolvedProjectC(projectId))

  def resolvedProjectC(projectId: ProjectId): ConnectionIO[ServerError.Valid[ResolvedProject]] =
    resolveProjectT(projectId).value.map(ServerError.fromEitherNel)

  private def resolveProjectT(
      projectId: ProjectId
  ): EitherT[ConnectionIO, NonEmptyList[ServerError], ResolvedProject] = {
    for {
      project <- fetchT(projectId)
      resolvedProjectReferenceTasks <- project.projectReferenceTasks.traverse(reference =>
        resolveProjectT(reference.projectReference).map(project =>
          ResolvedTask.ProjectReference(
            id = reference.id,
            project = project,
            weight = reference.weight
          )
        )
      )
    } yield {
      val resolvedPlainTasks = project.plainTasks.map(plain =>
        ResolvedTask.Plain(
          id = plain.id,
          name = plain.name,
          taskKind = plain.taskKind,
          unit = plain.unit,
          progress = plain.progress,
          weight = plain.weight
        )
      )
      ResolvedProject(
        id = project.id,
        plainTasks = resolvedPlainTasks,
        projectReferenceTasks = resolvedProjectReferenceTasks,
        name = project.name,
        description = project.description,
        ownerId = project.ownerId,
        flatIfSingleTask = project.flatIfSingleTask,
        readAccessors = project.readAccessors,
        writeAccessors = project.writeAccessors
      )
    }
  }

  private def modifyUsersWithRights[AK, DBAccessK, DBAccessEntry](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId],
      accessors: Project => Access[AK],
      modifier: (Accessors, NonEmptySet[UserId]) => Accessors,
      setAccess: (
          ProjectId,
          Access[AK]
      ) => ConnectionIO[Access.DbRepresentation[DBAccessK, DBAccessEntry]]
  ): ConnectionIO[ServerError.Valid[Access.DbRepresentation[DBAccessK, DBAccessEntry]]] = {
    val transformer =
      for {
        project <- fetchT(projectId)
        updatedAccess <- ServerError.liftNelC(
          setAccess(
            projectId,
            accessors(project)
              .focus(_.accessors)
              .modify(modifier(_, userIds))
          )
        )
      } yield updatedAccess

    transformer.value.map(ServerError.fromEitherNel)
  }

  private def fetchT(projectId: ProjectId): EitherT[ConnectionIO, NonEmptyList[ServerError], Project] =
    EitherT(fetchC(projectId).map(_.toEither))

  private def setReadAccessC(
      projectId: ProjectId,
      projectAccess: Access[AccessKind.Read]
  ): ConnectionIO[Access.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]] =
    setAccess(projectReadAccessDAO, projectReadAccessEntryDAO)(projectId, projectAccess)

  private def setWriteAccessC(
      projectId: ProjectId,
      projectAccess: Access[AccessKind.Write]
  ): ConnectionIO[Access.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]] =
    setAccess(projectWriteAccessDAO, projectWriteAccessEntryDAO)(projectId, projectAccess)

  private def setAccess[AccessK, DBAccessK, DBAccessKey, DBAccessEntry, DBAccessEntryKey](
      daoFunctionsDBAccessK: DAOFunctions[DBAccessK, DBAccessKey],
      daoFunctionsDBAccessEntry: DAOFunctions[DBAccessEntry, DBAccessEntryKey]
  )(
      projectId: ProjectId,
      projectAccess: Access[AccessK]
  )(implicit
      accessToDB: AccessToDB[ProjectId, AccessK, DBAccessK, DBAccessEntry],
      accessFromDB: AccessFromDB[ProjectId, AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[Access.DbRepresentation[DBAccessK, DBAccessEntry]] = {
    val components = Access.DbRepresentation(projectId, projectAccess)
    (
      daoFunctionsDBAccessK.insertC(components.access),
      daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)
    ).mapN { (access, entries) =>
      Access.DbRepresentation[ProjectId, AccessK, DBAccessK, DBAccessEntry](
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

  private def toAccessors[AccessK, DBAccessK, DBAccessEntry](
      dbComponentsC: ConnectionIO[ServerError.Valid[Access.DbRepresentation[DBAccessK, DBAccessEntry]]]
  )(implicit
      accessFromDB: AccessFromDB[ProjectId, AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[ServerError.Valid[Accessors]] = dbComponentsC.map(_.map(Access.fromDb(_).accessors))

}

object ProjectService {

  def toDbRepresentation(project: Project): DbRepresentation =
    DbRepresentation(project)

  def fromDbRepresentation(
      dbComponents: DbRepresentation
  ): ServerError.Valid[Project] =
    (
      dbComponents.plainTasks.traverse(Task.Plain.fromRow),
      dbComponents.projectReferenceTasks.traverse(Task.ProjectReference.fromRow)
    )
      .mapN { (plainTasks, projectReferenceTasks) =>
        Project(
          id = ProjectId(dbComponents.project.id),
          plainTasks = plainTasks.toVector,
          projectReferenceTasks = projectReferenceTasks.toVector,
          name = dbComponents.project.name,
          description = dbComponents.project.description,
          ownerId = UserId(dbComponents.project.ownerId),
          flatIfSingleTask = dbComponents.project.flatIfSingleTask,
          readAccessors = Access.fromDb(dbComponents.readAccess),
          writeAccessors = Access.fromDb(dbComponents.writeAccess)
        )
      }

  sealed trait DbRepresentation {
    def project: db.models.Project
    def plainTasks: Seq[db.models.PlainTask]
    def projectReferenceTasks: Seq[db.models.ProjectReferenceTask]
    def readAccess: Access.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]
    def writeAccess: Access.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]
  }

  object DbRepresentation {

    private[ProjectService] case class Impl(
        override val project: db.models.Project,
        override val plainTasks: Seq[db.models.PlainTask],
        override val projectReferenceTasks: Seq[db.models.ProjectReferenceTask],
        override val readAccess: Access.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry],
        override val writeAccess: Access.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]
    ) extends DbRepresentation

    def apply(project: Project): DbRepresentation = {
      val plainTasks = project.plainTasks.map(Task.Plain.toRow(project.id, _))
      val projectReferenceTasks = project.projectReferenceTasks.map(Task.ProjectReference.toRow(project.id, _))
      Impl(
        project = db.models.Project(
          id = project.id.uuid,
          ownerId = project.ownerId.uuid,
          name = project.name,
          description = project.description,
          flatIfSingleTask = project.flatIfSingleTask
        ),
        plainTasks = plainTasks,
        projectReferenceTasks = projectReferenceTasks,
        readAccess = Access.toDb(project.id, project.readAccessors),
        writeAccess = Access.toDb(project.id, project.writeAccessors)
      )
    }

  }

}
