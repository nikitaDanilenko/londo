package services.project

import cats.data.{ EitherT, NonEmptyList, NonEmptySet, Validated }
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import db.generated.daos._
import db.keys.{ ProjectId, UserId }
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import db.{ DAOFunctions, Transactionally, keys }
import doobie.ConnectionIO
import errors.ServerError
import monocle.syntax.all._
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._
import services.task.Task
import cats.syntax.traverse._
import errors.ServerError.Valid

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

  def create[F[_]: Async: ContextShift](projectCreation: ProjectCreation): F[ServerError.Valid[Project]] =
    transactionally {
      for {
        createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(projectCreation))
        project <- projectDAO.insertC(ProjectService.toDbRepresentation(createdProject).project)
        readAccess <- setReadAccess(createdProject.id, createdProject.readAccessors)
        writeAccess <- setWriteAccess(createdProject.id, createdProject.writeAccessors)
      } yield {
        val createdProjectComponents = ProjectService.toDbRepresentation(createdProject)
        ProjectService.fromDbRepresentation(
          ProjectService.DbRepresentation.Impl(
            project = project,
            plainTasks = createdProjectComponents.plainTasks,
            projectReferenceTasks = createdProjectComponents.projectReferenceTasks,
            readAccess = readAccess,
            writeAccess = writeAccess
          )
        )
      }
    }

  def createC(projectCreation: ProjectCreation): ConnectionIO[ServerError.Valid[Project]] =
    for {
      createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(projectCreation))
      _ <- projectDAO.insertC(ProjectService.toDbRepresentation(createdProject).project)
      _ <- setReadAccess(createdProject.id, createdProject.readAccessors)
      _ <- setWriteAccess(createdProject.id, createdProject.writeAccessors)
      project <- fetchC(createdProject.id)
    } yield project

  def setReadAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Read]
  ): ConnectionIO[ProjectAccess.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]] =
    setAccess(projectReadAccessDAO, projectReadAccessEntryDAO)(projectId, projectAccess)

  def setWriteAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Write]
  ): ConnectionIO[ProjectAccess.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]] =
    setAccess(projectWriteAccessDAO, projectWriteAccessEntryDAO)(projectId, projectAccess)

  private def setAccess[AccessK, DBAccessK, DBAccessKey, DBAccessEntry, DBAccessEntryKey](
      daoFunctionsDBAccessK: DAOFunctions[DBAccessK, DBAccessKey],
      daoFunctionsDBAccessEntry: DAOFunctions[DBAccessEntry, DBAccessEntryKey]
  )(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessK]
  )(implicit
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry],
      accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[ProjectAccess.DbRepresentation[DBAccessK, DBAccessEntry]] = {
    val components = ProjectAccess.DbRepresentation(projectId, projectAccess)
    (
      daoFunctionsDBAccessK.insertC(components.access),
      daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)
    ).mapN { (access, entries) =>
      ProjectAccess.DbRepresentation[AccessK, DBAccessK, DBAccessEntry](
        projectId = accessFromDB.projectId(access),
        projectAccess = ProjectAccess.fromDb(
          ProjectAccess.DbRepresentation.fromComponents(
            access = access,
            accessEntries = entries
          )
        )
      )
    }
  }

  def delete[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[Project]] =
    transactionally(deleteC(projectId))

  def deleteC(projectId: ProjectId): ConnectionIO[ServerError.Valid[Project]] = {
    val transformer = for {
      project <- fetchT(projectId)
      _ <- EitherT.liftF[ConnectionIO, NonEmptyList[ServerError], db.models.Project](projectDAO.deleteC(projectId))
    } yield project

    transformer.value.map(Validated.fromEither)
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
      _ <- ServerError.liftC(projectDAO.replaceC(updatedRow))
      updatedWrittenProject <- fetchT(projectId)
    } yield updatedWrittenProject
    transformer.value.map(Validated.fromEither)
  }

  def fetch[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[Project]] =
    transactionally(fetchC(projectId))

  def fetchC(projectId: ProjectId): ConnectionIO[ServerError.Valid[Project]] = {
    val projectReadAccessId = projectId.asProjectReadAccessId
    val projectWriteAccessId = projectId.asProjectWriteAccessId

    def liftF[A](a: ConnectionIO[A]): EitherT[ConnectionIO, ServerError, A] =
      EitherT.liftF[ConnectionIO, ServerError, A](a)

    val action = for {
      projectRow <- EitherT.fromOptionF(projectDAO.findC(projectId), ServerError.Project.NotFound)
      plainTasks <- liftF(plainTaskDAO.findByProjectIdC(projectId.uuid))
      projectReferenceTasks <- liftF(projectReferenceTaskDAO.findByProjectIdC(projectId.uuid))
      readAccess <- liftF(projectReadAccessDAO.findC(projectReadAccessId))
      readAccessEntries <- liftF(projectReadAccessEntryDAO.findByProjectReadAccessIdC(projectReadAccessId.uuid))
      writeAccess <- liftF(projectWriteAccessDAO.findC(projectWriteAccessId))
      writeAccessEntries <- liftF(projectWriteAccessEntryDAO.findByProjectWriteAccessIdC(projectWriteAccessId.uuid))
    } yield {
      ProjectService.fromDbRepresentation(
        ProjectService.DbRepresentation.Impl(
          project = projectRow,
          plainTasks = plainTasks,
          projectReferenceTasks = projectReferenceTasks,
          readAccess = ProjectAccess.DbRepresentation.fromComponents(
            readAccess.getOrElse(db.models.ProjectReadAccess(projectId.uuid, isAllowList = false)),
            readAccessEntries
          ),
          writeAccess = ProjectAccess.DbRepresentation.fromComponents(
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

  def allowReadUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[Valid[ProjectAccess.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.allowUsers, setReadAccess)

  def allowWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[Valid[ProjectAccess.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.allowUsers, setWriteAccess)

  def blockReadUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[Valid[ProjectAccess.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.blockUsers, setReadAccess)

  def blockWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[Valid[ProjectAccess.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.blockUsers, setWriteAccess)

  private def modifyUsersWithRights[AK, DBAccessK, DBAccessEntry](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId],
      accessors: Project => ProjectAccess[AK],
      modifier: (Accessors, NonEmptySet[UserId]) => Accessors,
      setAccess: (
          ProjectId,
          ProjectAccess[AK]
      ) => ConnectionIO[ProjectAccess.DbRepresentation[DBAccessK, DBAccessEntry]]
  ): ConnectionIO[ServerError.Valid[ProjectAccess.DbRepresentation[DBAccessK, DBAccessEntry]]] = {
    val transformer =
      for {
        project <- fetchT(projectId)
        updatedAccess <- ServerError.liftC(
          setAccess(
            projectId,
            accessors(project)
              .focus(_.accessors)
              .modify(modifier(_, userIds))
          )
        )
      } yield updatedAccess

    transformer.value.map(Validated.fromEither)
  }

  private def fetchT(projectId: ProjectId): EitherT[ConnectionIO, NonEmptyList[ServerError], Project] =
    EitherT(fetchC(projectId).map(_.toEither))

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
          id = keys.ProjectId(dbComponents.project.id),
          plainTasks = plainTasks.toVector,
          projectReferenceTasks = projectReferenceTasks.toVector,
          name = dbComponents.project.name,
          description = dbComponents.project.description,
          ownerId = keys.UserId(dbComponents.project.ownerId),
          parentProjectId = dbComponents.project.parentProjectId.map(ProjectId.apply),
          flatIfSingleTask = dbComponents.project.flatIfSingleTask,
          readAccessors = ProjectAccess.fromDb(dbComponents.readAccess),
          writeAccessors = ProjectAccess.fromDb(dbComponents.writeAccess)
        )
      }

  sealed trait DbRepresentation {
    def project: db.models.Project
    def plainTasks: Seq[db.models.PlainTask]
    def projectReferenceTasks: Seq[db.models.ProjectReferenceTask]
    def readAccess: ProjectAccess.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry]
    def writeAccess: ProjectAccess.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]
  }

  object DbRepresentation {

    private[ProjectService] case class Impl(
        override val project: db.models.Project,
        override val plainTasks: Seq[db.models.PlainTask],
        override val projectReferenceTasks: Seq[db.models.ProjectReferenceTask],
        override val readAccess: ProjectAccess.DbRepresentation[ProjectReadAccess, ProjectReadAccessEntry],
        override val writeAccess: ProjectAccess.DbRepresentation[ProjectWriteAccess, ProjectWriteAccessEntry]
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
          parentProjectId = project.parentProjectId.map(_.uuid),
          flatIfSingleTask = project.flatIfSingleTask
        ),
        plainTasks = plainTasks,
        projectReferenceTasks = projectReferenceTasks,
        readAccess = ProjectAccess.toDb(project.id, project.readAccessors),
        writeAccess = ProjectAccess.toDb(project.id, project.writeAccessors)
      )
    }

  }

}
