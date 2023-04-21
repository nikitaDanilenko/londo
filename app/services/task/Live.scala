package services.task

import cats.Applicative
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.project.ProjectKey
import db.daos.task.DAO
import db.generated.Tables
import db.{ ProjectId, TaskId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.DBError
import services.common.Transactionally.syntax._
import services.project.ProjectService
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._
import utils.collection.MapUtil
import utils.transformer.implicits._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: TaskService.Companion
)(implicit
    executionContext: ExecutionContext
) extends TaskService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, projectId: ProjectId): Future[Seq[Task]] =
    db.runTransactionally(companion.all(userId, projectId))

  override def create(
      userId: UserId,
      projectId: ProjectId,
      creation: Creation
  ): Future[ServerError.Or[Task]] =
    db.runTransactionally(
      companion.create(userId, projectId, creation)
    ).map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Task.Plain.Create(error.getMessage).asServerError)
      }

  override def update(
      userId: UserId,
      taskId: TaskId,
      update: Update
  ): Future[ServerError.Or[Task]] =
    db.runTransactionally(companion.update(userId, taskId, update))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Task.Plain.Update(error.getMessage).asServerError)
      }

  override def delete(userId: UserId, taskId: TaskId): Future[Boolean] =
    db.runTransactionally(companion.delete(userId, taskId))
      .recover { _ =>
        false
      }

}

object Live {

  class Companion @Inject() (
      taskDao: DAO,
      projectDao: db.daos.project.DAO
  ) extends TaskService.Companion {

    override def all(
        userId: UserId,
        projectId: ProjectId
    )(implicit
        ec: ExecutionContext
    ): DBIO[Seq[Task]] =
      for {
        exists <- projectDao.exists(ProjectKey(userId, projectId))
        tasks <-
          if (exists)
            taskDao
              .findAllFor(Seq(projectId))
              .map(_.map(_.transformInto[Task]))
          else Applicative[DBIO].pure(Seq.empty)
      } yield tasks

    override def allFor(userId: UserId, projectIds: Seq[ProjectId])(implicit
        ec: ExecutionContext
    ): DBIO[Map[ProjectId, Seq[Task]]] = {
      for {
        matchingProjects <- projectDao.allOf(userId, projectIds)
        typedIds = matchingProjects.map(_.id.transformInto[ProjectId])
        allPlainTasks <- taskDao.findAllFor(typedIds)
      } yield {
        // GroupBy skips projects with no entries, hence they are added manually afterwards.
        val preMap = allPlainTasks.groupBy(_.projectId.transformInto[ProjectId])
        MapUtil
          .unionWith(preMap, typedIds.map(_ -> Seq.empty).toMap)((x, _) => x)
          .view
          .mapValues(_.map(_.transformInto[Task]))
          .toMap
      }
    }

    override def create(
        userId: UserId,
        projectId: ProjectId,
        creation: Creation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Task] =
      ifProjectExists(userId, projectId) {
        for {
          task <- Creation.create(creation).to[DBIO]
          taskRow = (task, projectId).transformInto[Tables.TaskRow]
          inserted <- taskDao
            .insert(taskRow)
            .map(_.transformInto[Task])
        } yield inserted
      }

    override def update(
        userId: UserId,
        taskId: TaskId,
        update: Update
    )(implicit
        ec: ExecutionContext
    ): DBIO[Task] = {
      val findAction =
        OptionT(taskDao.find(taskId))
          .getOrElseF(DBIO.failed(DBError.Project.TaskNotFound))
      for {
        taskRow <- findAction
        _ <- ifProjectExists(userId, taskRow.projectId.transformInto[ProjectId]) {
          for {
            updated <- Update.update(taskRow.transformInto[Task], update).to[DBIO]
            row = (taskRow.projectId.transformInto[ProjectId], updated).transformInto[Tables.TaskRow]
            _ <- taskDao.update(row)
          } yield ()

        }
        updatedPlainTaskRow <- findAction
      } yield updatedPlainTaskRow.transformInto[Task]
    }

    override def delete(
        userId: UserId,
        taskId: TaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean] =
      OptionT(
        taskDao.find(taskId)
      ).map(_.projectId)
        .semiflatMap(projectId =>
          ifProjectExists(userId, projectId.transformInto[ProjectId]) {
            taskDao
              .delete(taskId)
              .map(_ > 0)
          }
        )
        .getOrElse(false)

    private def ifProjectExists[A](
        userId: UserId,
        id: ProjectId
    )(action: => DBIO[A])(implicit ec: ExecutionContext): DBIO[A] =
      projectDao.exists(ProjectKey(userId, id)).flatMap(exists => if (exists) action else ProjectService.notFound)

  }

}
