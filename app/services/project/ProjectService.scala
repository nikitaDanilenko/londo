package services.project

import cats.effect.{ Async, ContextShift }
import cats.syntax.contravariantSemigroupal._
import db.DAOFunctions
import db.generated.daos._
import db.keys.ProjectId
import doobie.ConnectionIO
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    projectReadAccessDAO: ProjectReadAccessDAO,
    projectReadAccessEntryDAO: ProjectReadAccessEntryDAO,
    projectWriteAccessDAO: ProjectWriteAccessDAO,
    projectWriteAccessEntryDAO: ProjectWriteAccessEntryDAO
) {

  def create[F[_]: Async: ContextShift](projectCreation: ProjectCreation): F[Project] = {
    val action = for {
      createdProject <- Async[ConnectionIO].liftIO(ProjectCreation.create(projectCreation))
      dbComponents = Project.toRow(createdProject)
      project <- projectDAO.insertC(dbComponents.project)
      readAccess <- setReadAccess(createdProject.id, createdProject.readAccessors)
      writeAccess <- setWriteAccess(createdProject.id, createdProject.writeAccessors)
    } yield project
    ???
  }

  def setReadAccess[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Read]
  ): ConnectionIO[ProjectAccess[AccessKind.Read]] =
    setAccess(projectReadAccessDAO, projectReadAccessEntryDAO)(projectId, _.asProjectReadAccessId, projectAccess)

  def setWriteAccess[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Write]
  ): ConnectionIO[ProjectAccess[AccessKind.Write]] =
    setAccess(projectWriteAccessDAO, projectWriteAccessEntryDAO)(projectId, _.asProjectWriteAccessId, projectAccess)

  private def setAccess[F[_], AccessK, DBAccessK, DBAccessKey, DBAccessEntry, DBAccessEntryKey](
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

  def delete = ???
  def update = ???
  def fetch = ???

  def createTask = ???
  def removeTask = ???
  def updateTask = ???

}
