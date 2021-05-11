package services.project

import cats.data.{ EitherT, NonEmptyList, NonEmptySet, Validated }
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import db.generated.daos._
import db.keys.{ ProjectId, UserId }
import db.{ DAOFunctions, Transactionally }
import doobie.ConnectionIO
import errors.ServerError
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._
import services.task.Task
import monocle.syntax.all._

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    projectReadAccessDAO: ProjectReadAccessDAO,
    projectReadAccessEntryDAO: ProjectReadAccessEntryDAO,
    projectWriteAccessDAO: ProjectWriteAccessDAO,
    projectWriteAccessEntryDAO: ProjectWriteAccessEntryDAO,
    taskDAO: TaskDAO,
    transactionally: Transactionally
) {

  def create[F[_]: Async: ContextShift](projectCreation: ProjectCreation): F[ServerError.Valid[Project]] =
    transactionally {
      for {
        createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(projectCreation))
        project <- projectDAO.insertC(Project.toRow(createdProject).project)
        readAccessors <- setReadAccess(createdProject.id, createdProject.readAccessors)
        writeAccessors <- setWriteAccess(createdProject.id, createdProject.writeAccessors)
      } yield Project.fromRow(
        Project.DbComponents.fromComponents(
          project = project,
          tasks = createdProject.tasks.map(Task.toRow(createdProject.id, _)),
          readAccessors = readAccessors,
          writeAccessors = writeAccessors
        )
      )
    }

  def setReadAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Read]
  ): ConnectionIO[ProjectAccess[AccessKind.Read]] =
    setAccess(projectReadAccessDAO, projectReadAccessEntryDAO)(projectId, projectAccess)

  def setWriteAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Write]
  ): ConnectionIO[ProjectAccess[AccessKind.Write]] =
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
  ): ConnectionIO[ProjectAccess[AccessK]] = {
    val dbAction: ConnectionIO[ProjectAccess.DbComponents[DBAccessK, DBAccessEntry]] = {
      val components = ProjectAccess.DbComponents(projectId, projectAccess)
      (
        daoFunctionsDBAccessK.insertC(components.access),
        daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)
      ).mapN { (access, entries) =>
        ProjectAccess.DbComponents[AccessK, DBAccessK, DBAccessEntry](
          projectId = accessFromDB.projectId(access),
          projectAccess = ProjectAccess.fromDb(
            ProjectAccess.DbComponents.fromComponents(
              access = access,
              accessEntries = entries
            )
          )
        )
      }
    }
    dbAction.map(ProjectAccess.fromDb[AccessK, DBAccessK, DBAccessEntry])
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
      updatedRow = Project.toRow(updatedProject).project
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
      tasks <- liftF(taskDAO.findByProjectIdC(projectId.uuid))
      readAccess <- liftF(projectReadAccessDAO.findC(projectReadAccessId))
      readAccessEntries <- liftF(projectReadAccessEntryDAO.findByProjectReadAccessIdC(projectReadAccessId.uuid))
      writeAccess <- liftF(projectWriteAccessDAO.findC(projectWriteAccessId))
      writeAccessEntries <- liftF(projectWriteAccessEntryDAO.findByProjectWriteAccessIdC(projectWriteAccessId.uuid))
    } yield {
      Project.fromRow(
        Project.DbComponents.fromComponents(
          project = projectRow,
          tasks = tasks,
          readAccessors = ProjectAccess.fromDb(
            ProjectAccess.DbComponents.fromComponents(
              readAccess.getOrElse(db.models.ProjectReadAccess(projectId.uuid, isAllowList = false)),
              readAccessEntries
            )
          ),
          writeAccessors = ProjectAccess.fromDb(
            ProjectAccess.DbComponents.fromComponents(
              writeAccess.getOrElse(db.models.ProjectWriteAccess(projectId.uuid, isAllowList = false)),
              writeAccessEntries
            )
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
  ): ConnectionIO[ServerError.Valid[ProjectAccess[AccessKind.Read]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.allowUsers, setReadAccess)

  def allowWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[ProjectAccess[AccessKind.Write]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.allowUsers, setWriteAccess)

  def blockReadUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[ProjectAccess[AccessKind.Read]]] =
    modifyUsersWithRights(projectId, userIds, _.readAccessors, Accessors.blockUsers, setReadAccess)

  def blockWriteUsersC(
      projectId: ProjectId,
      userIds: NonEmptySet[UserId]
  ): ConnectionIO[ServerError.Valid[ProjectAccess[AccessKind.Write]]] =
    modifyUsersWithRights(projectId, userIds, _.writeAccessors, Accessors.blockUsers, setWriteAccess)

  private def modifyUsersWithRights[AK](
      projectId: ProjectId,
      userIds: NonEmptySet[UserId],
      accessors: Project => ProjectAccess[AK],
      modifier: (Accessors, NonEmptySet[UserId]) => Accessors,
      setAccess: (ProjectId, ProjectAccess[AK]) => ConnectionIO[ProjectAccess[AK]]
  ): ConnectionIO[ServerError.Valid[ProjectAccess[AK]]] = {
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
