package services.project

import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.daos.project.ProjectKey
import db.generated.Tables
import db.{ ProjectId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.common.Transactionally.syntax._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: ProjectService.Companion
)(implicit
    executionContext: ExecutionContext
) extends ProjectService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def all(userId: UserId): Future[Seq[Project]] =
    db.runTransactionally(companion.all(userId))

  override def get(userId: UserId, id: ProjectId): Future[Option[Project]] =
    db.runTransactionally(companion.get(userId, id))

  override def create(userId: UserId, creation: Creation): Future[ServerError.Or[Project]] =
    db.runTransactionally(companion.create(userId, creation))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Project.Create(error.getMessage).asServerError)
      }

  override def update(
      userId: UserId,
      projectId: ProjectId,
      update: Update
  ): Future[ServerError.Or[Project]] =
    db.runTransactionally(companion.update(userId, projectId, update))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Project.Update(error.getMessage).asServerError)
      }

  override def delete(userId: UserId, id: ProjectId): Future[ServerError.Or[Boolean]] =
    db.runTransactionally(companion.delete(userId, id))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Project.Delete(error.getMessage).asServerError)
      }

}

object Live {

  class Companion @Inject() (
      dao: db.daos.project.DAO
  ) extends ProjectService.Companion {

    override def all(userId: UserId)(implicit ec: ExecutionContext): DBIO[Seq[Project]] =
      dao
        .findAllFor(userId)
        .map(
          _.map(_.transformInto[Project])
        )

    override def get(userId: UserId, id: ProjectId)(implicit ec: ExecutionContext): DBIO[Option[Project]] =
      OptionT(
        dao.find(ProjectKey(userId, id))
      )
        .map(_.transformInto[Project])
        .value

    override def allOf(userId: UserId, ids: Seq[ProjectId])(implicit ec: ExecutionContext): DBIO[Seq[Project]] =
      dao
        .allOf(userId, ids)
        .map(_.map(_.transformInto[Project]))

    override def create(
        ownerId: UserId,
        creation: Creation
    )(implicit
        ec: ExecutionContext
    ): DBIO[Project] = for {
      project <- Creation.create(ownerId, creation).to[DBIO]
      projectRow = (project, ownerId).transformInto[Tables.ProjectRow]
      inserted <- dao.insert(projectRow)
    } yield inserted.transformInto[Project]

    override def update(
        userId: UserId,
        projectId: ProjectId,
        update: Update
    )(implicit
        ec: ExecutionContext
    ): DBIO[Project] = {
      val findAction = OptionT(get(userId, projectId)).getOrElseF(ProjectService.notFound)
      for {
        project        <- findAction
        updated        <- Update.update(project, update).to[DBIO]
        _              <- dao.update(updated.transformInto[Tables.ProjectRow])
        updatedProject <- findAction
      } yield updatedProject
    }

    override def delete(userId: UserId, id: ProjectId)(implicit ec: ExecutionContext): DBIO[Boolean] =
      dao
        .delete(ProjectKey(userId, id))
        .map(_ > 0)

  }

}
