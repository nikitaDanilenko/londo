package services.task.reference

import db.{ ProjectId, ReferenceTaskId, UserId }
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait ReferenceTaskService {

  def all(userId: UserId, projectId: ProjectId): Future[Seq[ReferenceTask]]

  def add(
      userId: UserId,
      projectId: ProjectId,
      referenceTaskCreation: ReferenceTaskCreation
  ): Future[ServerError.Or[ReferenceTask]]

  def remove(userId: UserId, referenceTaskId: ReferenceTaskId): Future[Boolean]
}

object ReferenceTaskService {

  trait Companion {

    def all(
        userId: UserId,
        projectId: ProjectId
    )(implicit ec: ExecutionContext): DBIO[Seq[ReferenceTask]]

    def allFor(
        userId: UserId,
        projectIds: Seq[ProjectId]
    )(implicit ec: ExecutionContext): DBIO[Map[ProjectId, Seq[ReferenceTask]]]

    def add(
        userId: UserId,
        projectId: ProjectId,
        referenceTaskCreation: ReferenceTaskCreation
    )(implicit
        ec: ExecutionContext
    ): DBIO[ReferenceTask]

    def remove(
        userId: UserId,
        id: ReferenceTaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

}
