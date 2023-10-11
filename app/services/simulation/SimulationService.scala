package services.simulation

import db.{ DashboardId, TaskId, UserId }
import errors.ServerError
import services.DBError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait SimulationService {

  def all(userId: UserId, dashboardId: DashboardId): Future[Map[TaskId, Simulation]]

  def create(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      creation: Creation
  ): Future[ServerError.Or[Simulation]]

  def update(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      update: Update
  ): Future[ServerError.Or[Simulation]]

  def delete(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId
  ): Future[ServerError.Or[Boolean]]

}

object SimulationService {

  trait Companion {

    def all(
        userId: UserId,
        dashboardId: DashboardId
    )(implicit ec: ExecutionContext): DBIO[Map[TaskId, Simulation]]

    def create(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        creation: Creation
    )(implicit ec: ExecutionContext): DBIO[Simulation]

    def update(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[Simulation]

    def delete(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

  def notFound[A]: DBIO[A] = DBIO.failed(DBError.Simulation.NotFound)

}
