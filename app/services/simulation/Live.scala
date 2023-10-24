package services.simulation

import cats.Applicative
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.dashboard.DashboardKey
import db.daos.dashboardEntry.DashboardEntryKey
import db.daos.project.ProjectKey
import db.daos.simulation.SimulationKey
import db.generated.Tables
import db.{ DashboardId, ProjectId, TaskId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl.TransformerOps
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.DBError
import services.common.Transactionally.syntax._
import services.task.Task
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._
import utils.transformer.implicits._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: SimulationService.Companion
)(implicit executionContext: ExecutionContext)
    extends SimulationService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, dashboardId: DashboardId): Future[Map[TaskId, Simulation]] =
    db.runTransactionally(companion.all(userId, dashboardId))

  override def upsert(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      simulation: IncomingSimulation
  ): Future[ServerError.Or[Simulation]] =
    db.runTransactionally(companion.upsert(userId, dashboardId, taskId, simulation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Simulation.Upsert(error.getMessage).asServerError)
      }

  override def delete(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId
  ): Future[ServerError.Or[Boolean]] =
    db.runTransactionally(companion.delete(userId, dashboardId, taskId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Simulation.Delete(error.getMessage).asServerError)
      }

}

object Live {

  class Companion @Inject() (
      simulationDao: db.daos.simulation.DAO,
      dashboardDao: db.daos.dashboard.DAO,
      dashboardEntryDao: db.daos.dashboardEntry.DAO,
      taskDao: db.daos.task.DAO,
      projectDao: db.daos.project.DAO
  ) extends SimulationService.Companion {

    override def all(
        userId: UserId,
        dashboardId: DashboardId
    )(implicit
        ec: ExecutionContext
    ): DBIO[Map[TaskId, Simulation]] =
      for {
        dashboardExists <- dashboardDao.exists(DashboardKey(userId, dashboardId))
        simulations <-
          if (dashboardExists) simulationDao.findAllFor(dashboardId)
          else Applicative[DBIO].pure(Seq.empty[Tables.SimulationRow])
      } yield simulations
        .map(simulation => simulation.taskId.transformInto[TaskId] -> simulation.transformInto[Simulation])
        .toMap

    override def upsert(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        simulation: IncomingSimulation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Simulation] = {
      ifTaskExists(userId, dashboardId, taskId) { taskRow =>
        val progress = taskRow.transformInto[Task].progress
        for {
          maybeRow <- simulationDao.find(SimulationKey(taskId, dashboardId))
          simulation <- maybeRow.fold {
            for {
              simulation <- Creation.create(progress, Creation.from(simulation)).to[DBIO]
              simulationRow = (simulation, taskId, dashboardId).transformInto[Tables.SimulationRow]
              inserted <- simulationDao.insert(simulationRow)
            } yield inserted.transformInto[Simulation]
          } { simulationRow =>
            for {
              updated <- Update
                .update(simulationRow.transformInto[Simulation], progress, Update.from(simulation))
                .to[DBIO]
              updatedRow = (updated, taskId, dashboardId).transformInto[Tables.SimulationRow]
              _ <- simulationDao.update(updatedRow)
            } yield updatedRow.transformInto[Simulation]
          }
        } yield simulation
      }
    }

    override def delete(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId
    )(implicit
        ec: ExecutionContext
    ): DBIO[Boolean] = OptionT(
      simulationDao.find(SimulationKey(taskId, dashboardId))
    ).semiflatMap { _ =>
      ifTaskExists(userId, dashboardId, taskId) { _ =>
        simulationDao.delete(SimulationKey(taskId, dashboardId)).map(_ > 0)
      }
    }.getOrElseF(DBIO.successful(false))

    private def ifTaskExists[A](
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId
    )(action: Tables.TaskRow => DBIO[A])(implicit ec: ExecutionContext): DBIO[A] =
      for {
        dashboardExists <- dashboardDao.exists(DashboardKey(userId, dashboardId))
        task            <- OptionT(taskDao.find(taskId)).getOrElseF(DBIO.failed(DBError.Project.TaskNotFound))
        projectId = task.projectId.transformInto[ProjectId]
        projectExists      <- projectDao.exists(ProjectKey(userId, projectId))
        projectOnDashboard <- dashboardEntryDao.exists(DashboardEntryKey(dashboardId, projectId))
        result <-
          if (dashboardExists && projectExists && projectOnDashboard) action(task) else SimulationService.notFound
      } yield result

  }

}
