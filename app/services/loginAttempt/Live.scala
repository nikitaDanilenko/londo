package services.loginAttempt

import cats.data.OptionT
import db.UserId
import db.generated.Tables
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.DBError
import services.common.Transactionally.syntax._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import utils.DBIOUtil.instances._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: LoginAttemptService.Companion
)(implicit
    executionContext: ExecutionContext
) extends LoginAttemptService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def get(userId: UserId): Future[Option[LoginAttempt]] =
    db.runTransactionally(companion.get(userId))

  override def create(userId: UserId): Future[ServerError.Or[LoginAttempt]] =
    db.runTransactionally(companion.create(userId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Login.Create(error.getMessage).asServerError)
      }

  override def update(userId: UserId, update: Update): Future[ServerError.Or[LoginAttempt]] =
    db.runTransactionally(companion.update(userId, update))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Login.Update(error.getMessage).asServerError)
      }

}

object Live {

  class Companion @Inject() (
      dao: db.daos.loginAttempt.DAO
  ) extends LoginAttemptService.Companion {

    override def get(userId: UserId)(implicit ec: ExecutionContext): DBIO[Option[LoginAttempt]] =
      dao
        .find(userId)
        .map(_.map(_.transformInto[LoginAttempt]))

    override def create(
        userId: UserId
    )(implicit ec: ExecutionContext): DBIO[LoginAttempt] = {
      val attempt = Creation.create
      val row     = (attempt, userId).transformInto[Tables.LoginAttemptRow]
      for {
        inserted <- dao.insert(row)
      } yield inserted.transformInto[LoginAttempt]
    }

    override def update(
        userId: UserId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[LoginAttempt] = {
      val findAction = OptionT(get(userId)).getOrElseF(DBIO.failed(DBError.Login.NotFound))

      for {
        attempt <- findAction
        _ <- dao.update(
          (
            Update.update(attempt, update),
            userId
          )
            .transformInto[Tables.LoginAttemptRow]
        )
        updated <- findAction
      } yield updated
    }

  }

}
