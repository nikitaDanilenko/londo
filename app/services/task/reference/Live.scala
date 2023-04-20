package services.task.reference

import cats.Applicative
import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.project.ProjectKey
import db.generated.Tables
import db.{ ProjectId, ReferenceTaskId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl.TransformerOps
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.common.Transactionally.syntax._
import services.project.ProjectService
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._
import utils.collection.MapUtil

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: ReferenceTaskService.Companion
)(implicit
    executionContext: ExecutionContext
) extends ReferenceTaskService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId, projectId: ProjectId): Future[Seq[ReferenceTask]] =
    db.runTransactionally(companion.all(userId, projectId))

  override def create(
      userId: UserId,
      projectId: ProjectId,
      referenceTaskCreation: ReferenceTaskCreation
  ): Future[ServerError.Or[ReferenceTask]] =
    db.runTransactionally(companion.create(userId, projectId, referenceTaskCreation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Task.Reference.Create(error.getMessage).asServerError)
      }

  override def delete(userId: UserId, referenceTaskId: ReferenceTaskId): Future[Boolean] =
    db.runTransactionally(companion.delete(userId, referenceTaskId))
      .recover { case _ =>
        false
      }

}

object Live {

  class Companion @Inject() (
      referenceTaskDao: db.daos.task.reference.DAO,
      projectDao: db.daos.project.DAO
  ) extends ReferenceTaskService.Companion {

    override def all(userId: UserId, projectId: ProjectId)(implicit ec: ExecutionContext): DBIO[Seq[ReferenceTask]] =
      for {
        exists <- projectDao.exists(ProjectKey(userId, projectId))
        plainTasks <-
          if (exists)
            referenceTaskDao
              .findAllFor(Seq(projectId))
              .map(_.map(_.transformInto[ReferenceTask]).toList)
          else Applicative[DBIO].pure(List.empty)
      } yield plainTasks

    override def allFor(userId: UserId, projectIds: Seq[ProjectId])(implicit
        ec: ExecutionContext
    ): DBIO[Map[ProjectId, Seq[ReferenceTask]]] =
      for {
        matchingProjects <- projectDao.allOf(userId, projectIds)
        typedIds = matchingProjects.map(_.id.transformInto[ProjectId])
        allReferenceTasks <- referenceTaskDao.findAllFor(typedIds)
      } yield {
        // GroupBy skips projects with no entries, hence they are added manually afterwards.
        val preMap = allReferenceTasks.groupBy(_.projectId.transformInto[ProjectId])
        MapUtil
          .unionWith(preMap, typedIds.map(_ -> Seq.empty).toMap)((x, _) => x)
          .view
          .mapValues(_.map(_.transformInto[ReferenceTask]).toList)
          .toMap
      }

    override def create(userId: UserId, projectId: ProjectId, referenceTaskCreation: ReferenceTaskCreation)(implicit
                                                                                                            ec: ExecutionContext
    ): DBIO[ReferenceTask] = ifProjectExists(userId, projectId) {
      for {
        referenceTask <- ReferenceTaskCreation.create(referenceTaskCreation).to[DBIO]
        referenceTaskRow = (referenceTask, projectId).transformInto[Tables.ReferenceTaskRow]
        inserted <- referenceTaskDao
          .insert(referenceTaskRow)
          .map(_.transformInto[ReferenceTask])
      } yield inserted
    }

    override def delete(userId: UserId, id: ReferenceTaskId)(implicit ec: ExecutionContext): DBIO[Boolean] =
      OptionT(
        referenceTaskDao.find(id)
      ).map(_.projectId)
        .semiflatMap(projectId =>
          ifProjectExists(userId, projectId.transformInto[ProjectId]) {
            referenceTaskDao
              .delete(id)
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
