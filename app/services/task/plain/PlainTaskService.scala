package services.task.plain

import db.{ PlainTaskId, ProjectId, UserId }
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait PlainTaskService {

  def all(userId: UserId, projectId: ProjectId): Future[List[PlainTask]]

  def create(
      userId: UserId,
      projectId: ProjectId,
      plainTaskCreation: PlainTaskCreation
  ): Future[ServerError.Or[PlainTask]]

  def update(
      userId: UserId,
      plainTaskId: PlainTaskId,
      plainTaskUpdate: PlainTaskUpdate
  ): Future[ServerError.Or[PlainTask]]

  def delete(userId: UserId, plainTaskId: PlainTaskId): Future[Boolean]
}

object PlainTaskService {

  trait Companion {

    def all(
        userId: UserId,
        projectId: ProjectId
    )(implicit ec: ExecutionContext): DBIO[List[PlainTask]]

    def allFor(
        userId: UserId,
        projectIds: Seq[ProjectId]
    )(implicit ec: ExecutionContext): DBIO[Map[ProjectId, List[PlainTask]]]

    def create(
        userId: UserId,
        projectId: ProjectId,
        plainTaskCreation: PlainTaskCreation
    )(implicit
        ec: ExecutionContext
    ): DBIO[PlainTask]

    def update(
        userId: UserId,
        plainTaskId: PlainTaskId,
        plainTaskUpdate: PlainTaskUpdate
    )(implicit
        ec: ExecutionContext
    ): DBIO[PlainTask]

    def delete(
        userId: UserId,
        id: PlainTaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

}
