package services.simulation

import db.{ DashboardId, TaskId, UserId }
import errors.ServerError
import services.DBError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait SimulationService {

  def all(userId: UserId, dashboardId: DashboardId): Future[Map[TaskId, Simulation]]

  def upsert(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      simulation: IncomingSimulation
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

    def upsert(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        simulation: IncomingSimulation
    )(implicit ec: ExecutionContext): DBIO[Simulation]

    def delete(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean]

  }

  def notFound[A]: DBIO[A] = DBIO.failed(DBError.Simulation.NotFound)

}
