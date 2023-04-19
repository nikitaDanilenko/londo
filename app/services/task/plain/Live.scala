package services.task.plain

import cats.Applicative
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.project.ProjectKey
import db.generated.Tables
import db.{ PlainTaskId, ProjectId, UserId }
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
    companion: PlainTaskService.Companion
)(implicit
    executionContext: ExecutionContext
) extends PlainTaskService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, projectId: ProjectId): Future[List[PlainTask]] =
    db.runTransactionally(companion.all(userId, projectId))

  override def add(
      userId: UserId,
      projectId: ProjectId,
      plainTaskCreation: PlainTaskCreation
  ): Future[ServerError.Or[PlainTask]] =
    db.runTransactionally(
      companion.add(userId, projectId, plainTaskCreation)
    ).map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Task.Plain.Create(error.getMessage).asServerError)
      }

  override def update(
      userId: UserId,
      plainTaskId: PlainTaskId,
      plainTaskUpdate: PlainTaskUpdate
  ): Future[ServerError.Or[PlainTask]] =
    db.runTransactionally(companion.update(userId, plainTaskId, plainTaskUpdate))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Task.Plain.Update(error.getMessage).asServerError)
      }

  override def remove(userId: UserId, plainTaskId: PlainTaskId): Future[Boolean] =
    db.runTransactionally(companion.remove(userId, plainTaskId))
      .recover { _ =>
        false
      }

}

object Live {

  class Companion @Inject() (
      plainTaskDao: db.daos.task.plain.DAO,
      projectDao: db.daos.project.DAO
  ) extends PlainTaskService.Companion {

    override def all(
        userId: UserId,
        projectId: ProjectId
    )(implicit
        ec: ExecutionContext
    ): DBIO[List[PlainTask]] =
      for {
        exists <- projectDao.exists(ProjectKey(userId, projectId))
        plainTasks <-
          if (exists)
            plainTaskDao
              .findAllFor(Seq(projectId))
              .map(_.map(_.transformInto[PlainTask]).toList)
          else Applicative[DBIO].pure(List.empty)
      } yield plainTasks

    override def allFor(userId: UserId, projectIds: Seq[ProjectId])(implicit
        ec: ExecutionContext
    ): DBIO[Map[ProjectId, List[PlainTask]]] = {
      for {
        matchingRecipes <- projectDao.allOf(userId, projectIds)
        typedIds = matchingRecipes.map(_.id.transformInto[ProjectId])
        allPlainTasks <- plainTaskDao.findAllFor(typedIds)
      } yield {
        // GroupBy skips projects with no entries, hence they are added manually afterwards.
        val preMap = allPlainTasks.groupBy(_.projectId.transformInto[ProjectId])
        MapUtil
          .unionWith(preMap, typedIds.map(_ -> Seq.empty).toMap)((x, _) => x)
          .view
          .mapValues(_.map(_.transformInto[PlainTask]).toList)
          .toMap
      }
    }

    override def add(
        userId: UserId,
        projectId: ProjectId,
        plainTaskCreation: PlainTaskCreation
    )(implicit
        ec: ExecutionContext
    ): DBIO[PlainTask] =
      ifProjectExists(userId, projectId) {
        for {
          plainTask <- PlainTaskCreation.create(plainTaskCreation).to[DBIO]
          plainTaskRow = (plainTask, projectId).transformInto[Tables.PlainTaskRow]
          inserted <- plainTaskDao
            .insert(plainTaskRow)
            .map(_.transformInto[PlainTask])
        } yield inserted
      }

    override def update(
        userId: UserId,
        plainTaskId: PlainTaskId,
        plainTaskUpdate: PlainTaskUpdate
    )(implicit
        ec: ExecutionContext
    ): DBIO[PlainTask] = {
      val findAction =
        OptionT(plainTaskDao.find(plainTaskId))
          .getOrElseF(DBIO.failed(DBError.Project.TaskNotFound))
      for {
        plainTaskRow <- findAction
        _ <- ifProjectExists(userId, plainTaskRow.projectId.transformInto[ProjectId]) {
          plainTaskDao.update(
            (
              PlainTaskUpdate
                .update(plainTaskRow.transformInto[PlainTask], plainTaskUpdate),
              plainTaskRow.projectId.transformInto[ProjectId]
            )
              .transformInto[Tables.PlainTaskRow]
          )
        }
        updatedPlainTaskRow <- findAction
      } yield updatedPlainTaskRow.transformInto[PlainTask]
    }

    override def remove(
        userId: UserId,
        id: PlainTaskId
    )(implicit ec: ExecutionContext): DBIO[Boolean] = {
      OptionT(
        plainTaskDao.find(id)
      ).map(_.projectId)
        .semiflatMap(projectId =>
          ifProjectExists(userId, projectId.transformInto[ProjectId]) {
            plainTaskDao
              .delete(id)
              .map(_ > 0)
          }
        )
        .getOrElse(false)
    }

    private def ifProjectExists[A](
        userId: UserId,
        id: ProjectId
    )(action: => DBIO[A])(implicit ec: ExecutionContext): DBIO[A] =
      projectDao.exists(ProjectKey(userId, id)).flatMap(exists => if (exists) action else ProjectService.notFound)

  }

}
