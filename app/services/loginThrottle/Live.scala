package services.loginThrottle

import cats.data.OptionT
import cats.effect.unsafe.implicits.global
import db.UserId
import db.generated.Tables
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import services.DBError
import services.common.Transactionally.syntax._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.DBIOUtil.instances._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: LoginThrottleService.Companion
)(implicit
    executionContext: ExecutionContext
) extends LoginThrottleService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def get(userId: UserId): Future[Option[LoginThrottle]] =
    db.runTransactionally(companion.get(userId))

  override def create(userId: UserId): Future[ServerError.Or[LoginThrottle]] =
    db.runTransactionally(companion.create(userId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Login.Create(error.getMessage).asServerError)
      }

  override def update(userId: UserId, update: Update): Future[ServerError.Or[LoginThrottle]] =
    db.runTransactionally(companion.update(userId, update))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Login.Update(error.getMessage).asServerError)
      }

}

object Live {

  class Companion @Inject() (
      dao: db.daos.loginThrottle.DAO
  ) extends LoginThrottleService.Companion {

    override def get(userId: UserId)(implicit ec: ExecutionContext): DBIO[Option[LoginThrottle]] =
      dao
        .find(userId)
        .map(_.map(_.transformInto[LoginThrottle]))

    override def create(
        userId: UserId
    )(implicit ec: ExecutionContext): DBIO[LoginThrottle] = {
      for {
        loginThrottle <- Creation.create.to[DBIO]
        row = (loginThrottle, userId).transformInto[Tables.LoginThrottleRow]
        inserted <- dao.insert(row)
      } yield inserted.transformInto[LoginThrottle]
    }

    override def update(
        userId: UserId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[LoginThrottle] = {
      val findAction = OptionT(get(userId)).getOrElseF(DBIO.failed(DBError.Login.NotFound))

      for {
        attempt <- findAction
        _ <- dao.update(
          (
            Update.update(attempt, update),
            userId
          )
            .transformInto[Tables.LoginThrottleRow]
        )
        updated <- findAction
      } yield updated
    }

  }

}
