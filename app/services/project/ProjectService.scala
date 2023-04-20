package services.project

import db.{ ProjectId, UserId }
import errors.ServerError
import services.DBError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait ProjectService {

  def all(userId: UserId): Future[Seq[Project]]
  def get(userId: UserId, id: ProjectId): Future[Option[Project]]
  def create(userId: UserId, projectCreation: ProjectCreation): Future[ServerError.Or[Project]]
  def update(userId: UserId, projectId: ProjectId, projectUpdate: ProjectUpdate): Future[ServerError.Or[Project]]
  def delete(userId: UserId, id: ProjectId): Future[Boolean]
}

object ProjectService {

  trait Companion {

    def all(userId: UserId)(implicit ec: ExecutionContext): DBIO[Seq[Project]]

    def get(
        userId: UserId,
        id: ProjectId
    )(implicit ec: ExecutionContext): DBIO[Option[Project]]

    def allOf(
        userId: UserId,
        ids: Seq[ProjectId]
    )(implicit ec: ExecutionContext): DBIO[Seq[Project]]

    def create(
        ownerIds: UserId,
        projectCreation: ProjectCreation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Project]

    def update(
        userId: UserId,
        projectId: ProjectId,
        projectUpdate: ProjectUpdate
    )(implicit
        ec: ExecutionContext
    ): DBIO[Project]

    def delete(
        userId: UserId,
        id: ProjectId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

  def notFound[A]: DBIO[A] = DBIO.failed(DBError.Project.NotFound)

}
