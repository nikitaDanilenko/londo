package services.project

import cats.data.EitherT
import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import db.generated.daos._
import db.keys.ProjectId
import db.{ DAOFunctions, DbTransactorProvider }
import doobie.ConnectionIO
import doobie.implicits._
import errors.ServerError
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    projectReadAccessDAO: ProjectReadAccessDAO,
    projectReadAccessEntryDAO: ProjectReadAccessEntryDAO,
    projectWriteAccessDAO: ProjectWriteAccessDAO,
    projectWriteAccessEntryDAO: ProjectWriteAccessEntryDAO,
    taskDAO: TaskDAO,
    dbTransactorProvider: DbTransactorProvider
) {

  def create[F[_]: Async: ContextShift](projectCreation: ProjectCreation): F[ServerError.Valid[Project]] = {
    val action = for {
      createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(projectCreation))
      project <- projectDAO.insertC(Project.toRow(createdProject).project)
      readAccessors <- setReadAccess(createdProject.id, createdProject.readAccessors)
      writeAccessors <- setWriteAccess(createdProject.id, createdProject.writeAccessors)
    } yield Project.fromRow(
      Project.DbComponents.fromComponents(
        project = project,
        tasks = createdProject.tasks.map(Task.toRow),
        readAccessors = readAccessors,
        writeAccessors = writeAccessors
      )
    )
    action.transact(dbTransactorProvider.transactor[F])
  }

  def setReadAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Read]
  ): ConnectionIO[ProjectAccess[AccessKind.Read]] =
    setAccess(projectReadAccessDAO, projectReadAccessEntryDAO)(projectId, _.asProjectReadAccessId, projectAccess)

  def setWriteAccess(
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Write]
  ): ConnectionIO[ProjectAccess[AccessKind.Write]] =
    setAccess(projectWriteAccessDAO, projectWriteAccessEntryDAO)(projectId, _.asProjectWriteAccessId, projectAccess)

  private def setAccess[AccessK, DBAccessK, DBAccessKey, DBAccessEntry, DBAccessEntryKey](
      daoFunctionsDBAccessK: DAOFunctions[DBAccessK, DBAccessKey],
      daoFunctionsDBAccessEntry: DAOFunctions[DBAccessEntry, DBAccessEntryKey]
  )(
      projectId: ProjectId,
      accessIdOf: ProjectId => DBAccessKey,
      projectAccess: ProjectAccess[AccessK]
  )(implicit
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry],
      accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry]
  ): ConnectionIO[ProjectAccess[AccessK]] = {
    val dbAction: ConnectionIO[Option[ProjectAccess.DbComponents[DBAccessK, DBAccessEntry]]] =
      ProjectAccess.DbComponents(projectId, projectAccess) match {
        case Some(components) =>
          (
            daoFunctionsDBAccessK.insertC(components.access),
            daoFunctionsDBAccessEntry.insertAllC(components.accessEntries)
          ).mapN { (access, entries) =>
            ProjectAccess.DbComponents[AccessK, DBAccessK, DBAccessEntry](
              projectId = accessFromDB.projectId(access),
              projectAccess = ProjectAccess(Accessors.restricted(accessFromDB.entryUserIds(access, entries)))
            )
          }
        case None =>
          daoFunctionsDBAccessK
            .deleteC(accessIdOf(projectId))
            .map(_ => None: Option[ProjectAccess.DbComponents[DBAccessK, DBAccessEntry]])
      }
    dbAction.map(ProjectAccess.fromDb[AccessK, DBAccessK, DBAccessEntry])
  }

  // TODO: This function should return the complete old project
  def delete[F[_]: Async: ContextShift](projectId: ProjectId): F[db.models.Project] = projectDAO.delete[F](projectId)
  def update = ???

  def fetch[F[_]: Async: ContextShift](projectId: ProjectId): F[ServerError.Valid[Project]] = {
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
            readAccess.map(
              ProjectAccess.DbComponents.fromComponents(
                _,
                readAccessEntries
              )
            )
          ),
          writeAccessors = ProjectAccess.fromDb(
            writeAccess.map(
              ProjectAccess.DbComponents.fromComponents(
                _,
                writeAccessEntries
              )
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
      .transact(dbTransactorProvider.transactor[F])
  }

  def createTask = ???
  def removeTask = ???
  def updateTask = ???

}
