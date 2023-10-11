package services.simulation

import cats.Applicative
import cats.data.OptionT
import db.daos.dashboard.DashboardKey
import db.daos.project.ProjectKey
import db.generated.Tables
import db.{ DashboardId, ProjectId, TaskId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl.TransformerOps
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.DBError
import slick.jdbc.PostgresProfile
import cats.effect.unsafe.implicits.global
import db.daos.dashboardEntry.DashboardEntryKey
import db.daos.simulation.SimulationKey

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._
import services.common.Transactionally.syntax._
import slick.dbio.DBIO
import utils.transformer.implicits._

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: SimulationService.Companion
)(implicit executionContext: ExecutionContext)
    extends SimulationService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, dashboardId: DashboardId): Future[Map[TaskId, Simulation]] =
    db.runTransactionally(companion.all(userId, dashboardId))

  override def create(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      creation: Creation
  ): Future[ServerError.Or[Simulation]] =
    db.runTransactionally(companion.create(userId, dashboardId, taskId, creation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Simulation.Create(error.getMessage).asServerError)
      }

  override def update(
      userId: UserId,
      dashboardId: DashboardId,
      taskId: TaskId,
      update: Update
  ): Future[ServerError.Or[Simulation]] =
    db.runTransactionally(companion.update(userId, dashboardId, taskId, update))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Simulation.Update(error.getMessage).asServerError)
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

    override def create(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        creation: Creation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Simulation] = ifTaskExists(userId, dashboardId, taskId) {
      for {
        simulation <- Creation.create(creation).to[DBIO]
        simulationRow = (simulation, taskId, dashboardId).transformInto[Tables.SimulationRow]
        inserted <- simulationDao.insert(simulationRow)
      } yield inserted.transformInto[Simulation]
    }

    override def update(
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId,
        update: Update
    )(implicit
        ec: ExecutionContext
    ): DBIO[Simulation] = {
      val findAction =
        OptionT(simulationDao.find(SimulationKey(taskId, dashboardId))).getOrElseF(SimulationService.notFound)
      for {
        simulationRow <- findAction
        _ <- ifTaskExists(userId, dashboardId, taskId) {
          for {
            updated <- Update.update(simulationRow.transformInto[Simulation], update).to[DBIO]
            updatedRow = (updated, taskId, dashboardId).transformInto[Tables.SimulationRow]
            _ <- simulationDao.update(updatedRow)
          } yield ()
        }
        updatedSimulationRow <- findAction
      } yield updatedSimulationRow.transformInto[Simulation]
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
      ifTaskExists(userId, dashboardId, taskId) {
        simulationDao.delete(SimulationKey(taskId, dashboardId)).map(_ > 0)
      }
    }.getOrElseF(DBIO.successful(false))

    private def ifTaskExists[A](
        userId: UserId,
        dashboardId: DashboardId,
        taskId: TaskId
    )(action: => DBIO[A])(implicit ec: ExecutionContext): DBIO[A] =
      for {
        dashboardExists <- dashboardDao.exists(DashboardKey(userId, dashboardId))
        task            <- OptionT(taskDao.find(taskId)).getOrElseF(DBIO.failed(DBError.Project.TaskNotFound))
        projectId = task.projectId.transformInto[ProjectId]
        projectExists      <- projectDao.exists(ProjectKey(userId, projectId))
        projectOnDashboard <- dashboardEntryDao.exists(DashboardEntryKey(dashboardId, projectId))
        result <- if (dashboardExists && projectExists && projectOnDashboard) action else SimulationService.notFound
      } yield result

  }

}
