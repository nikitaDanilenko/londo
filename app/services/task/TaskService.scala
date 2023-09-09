package services.task

import db.{ ProjectId, TaskId, UserId }
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait TaskService {

  def all(userId: UserId, projectId: ProjectId): Future[Seq[Task]]

  def allFor(userId: UserId, projectIds: Seq[ProjectId]): Future[Map[ProjectId, Seq[Task]]]

  def create(
      userId: UserId,
      projectId: ProjectId,
      creation: Creation
  ): Future[ServerError.Or[Task]]

  def update(
      userId: UserId,
      taskId: TaskId,
      update: Update
  ): Future[ServerError.Or[Task]]

  def delete(userId: UserId, taskId: TaskId): Future[ServerError.Or[Boolean]]
}

object TaskService {

  trait Companion {

    def all(
        userId: UserId,
        projectId: ProjectId
    )(implicit ec: ExecutionContext): DBIO[Seq[Task]]

    def allFor(
        userId: UserId,
        projectIds: Seq[ProjectId]
    )(implicit ec: ExecutionContext): DBIO[Map[ProjectId, Seq[Task]]]

    def create(
        userId: UserId,
        projectId: ProjectId,
        creation: Creation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Task]

    def update(
        userId: UserId,
        taskId: TaskId,
        update: Update
    )(implicit
        ec: ExecutionContext
    ): DBIO[Task]

    def delete(
        userId: UserId,
        taskId: TaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

}
