package services.project

import cats.effect.{ Async, ContextShift }
import db.generated.daos.{
  ProjectDAO,
  ProjectReadAccessDAO,
  ProjectReadAccessEntryDAO,
  ProjectWriteAccessDAO,
  ProjectWriteAccessEntryDAO
}
import cats.syntax.flatMap._
import cats.syntax.functor._
import AccessToDB.instances._
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry }
import AccessFromDB.instances._
import db.DAOFunctions
import db.DAOFunctions._
import db.keys.{ ProjectId, UserId }

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    implicit val projectReadAccessDAO: ProjectReadAccessDAO,
    projectReadAccessEntryDAO: ProjectReadAccessEntryDAO,
    projectWriteAccessDAO: ProjectWriteAccessDAO,
    projectWriteAccessEntryDAO: ProjectWriteAccessEntryDAO
) {

  def create[F[_]: Async: ContextShift](projectCreation: ProjectCreation): F[Project] = {
    for {
      createdProject <- Async[F].liftIO(ProjectCreation.create(projectCreation))
      dbComponents = Project.toRow(createdProject)
      project <- projectDAO.insert(dbComponents.project)
      readAccess <- setReadAccess(createdProject.id, createdProject.readAccessors)
    } yield project
    ???
  }

  def setReadAccess[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessKind.Read]
  ): F[ProjectAccess[AccessKind.Read]] = {
//    setAccess(projectId, projectAccess)
    ???
  }

  def setAccess[F[_], AccessK, DBAccessK, DBAccessEntry](
      projectId: ProjectId,
      projectAccess: ProjectAccess[AccessK]
  )(implicit
      asyncF: Async[F],
      contextShiftF: ContextShift[F],
      accessToDB: AccessToDB[AccessK, DBAccessK, DBAccessEntry],
      accessFromDB: AccessFromDB[AccessK, DBAccessK, DBAccessEntry],
      daoFunctionsDBAccessK: DAOFunctions[DBAccessK],
      daoFunctionsDBAccessEntry: DAOFunctions[DBAccessEntry]
  ): F[ProjectAccess[AccessK]] = {
    val dbAction: F[Option[ProjectAccess.DbComponents[DBAccessK, DBAccessEntry]]] =
      ProjectAccess.DbComponents(projectId, projectAccess) match {
        case Some(components) =>
          for {
            access <- daoFunctionsDBAccessK.insert(components.access)
            entries <- daoFunctionsDBAccessEntry.insertAll(components.accessEntries)
            // Todo: The type annotation should be unnecessary
          } yield ProjectAccess.DbComponents[AccessK, DBAccessK, DBAccessEntry](
            projectId = accessFromDB.projectId(access),
            projectAccess = ProjectAccess(Accessors.restricted(accessFromDB.entryUserIds(access, entries)))
          )
        case None =>
          projectReadAccessDAO
            .delete(projectId.uuid)
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
