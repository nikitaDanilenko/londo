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
import services.user.UserId
import AccessToDB.instances._
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry }
import AccessFromDB.instances._

import javax.inject.Inject

class ProjectService @Inject() (
    projectDAO: ProjectDAO,
    projectReadAccessDAO: ProjectReadAccessDAO,
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
    val dbAction =
      ProjectAccess.DbComponents(projectId, projectAccess) match {
        case Some(readComponents) =>
          for {
            readAccess <- projectReadAccessDAO.insert(readComponents.access)
            entries <- projectReadAccessEntryDAO.insertAll(readComponents.accessEntries)
            // Todo: The type annotation should be unnecessary
          } yield ProjectAccess.DbComponents[AccessKind.Read, ProjectReadAccess, ProjectReadAccessEntry](
            projectId = ProjectId(readAccess.projectId),
            // TODO: Use a more convenient user id extraction (via implicits?)?
            projectAccess = ProjectAccess(Accessors.restricted(entries.map(e => UserId(e.userId)).toSet))
          )
        case None =>
          projectReadAccessDAO.delete(projectId.uuid).map(_ => None)
      }
    dbAction.map(ProjectAccess.fromDb)
  }

  def delete = ???
  def update = ???
  def fetch = ???

  def createTask = ???
  def removeTask = ???
  def updateTask = ???

}
