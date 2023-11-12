package services.session

import cats.effect.unsafe.implicits.global
import db.daos.session.SessionKey
import db.generated.Tables
import db.{ SessionId, UserId }
import errors.{ ErrorContext, ServerError }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import security.jwt.JwtConfiguration
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import slickeffect.catsio.implicits._
import utils.transformer.implicits._

import java.sql.Timestamp
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: SessionService.Companion
)(implicit
    executionContext: ExecutionContext
) extends SessionService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def create(userId: UserId): Future[ServerError.Or[Session]] =
    db.run(companion.create(userId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Session.Create(error.getMessage).asServerError)
      }

  override def delete(userId: UserId, sessionId: SessionId): Future[ServerError.Or[Boolean]] =
    db.run(companion.delete(userId, sessionId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Session.Delete(error.getMessage).asServerError)
      }

  override def deleteAll(userId: UserId): Future[ServerError.Or[Boolean]] =
    db.run(companion.deleteAll(userId))
      .map(Right(_))
      .recover { case error =>
        Left(ErrorContext.Session.Delete(error.getMessage).asServerError)
      }

  override def exists(userId: UserId, sessionId: SessionId): Future[Boolean] =
    db.run(companion.exists(userId, sessionId))

}

object Live {

  class Companion @Inject() (
      dao: db.daos.session.DAO,
      jwtConfiguration: JwtConfiguration
  ) extends SessionService.Companion {

    private val allowedValidityInDays: Int =
      Math.ceil(jwtConfiguration.restrictedDurationInSeconds.toDouble / 86400).toInt

    override def create(userId: UserId)(implicit
        executionContext: ExecutionContext
    ): DBIO[Session] = {
      for {
        session <- Creation.create.to[DBIO]
        _ <- dao
          .deleteAllBefore(
            userId,
            session.createdAt
              .minus(allowedValidityInDays, ChronoUnit.DAYS)
              .transformInto[Timestamp]
          )
        inserted <- dao
          .insert((session, userId).transformInto[Tables.SessionRow])
      } yield inserted.transformInto[Session]
    }

    override def delete(userId: UserId, sessionId: SessionId)(implicit
        executionContext: ExecutionContext
    ): DBIO[Boolean] =
      dao
        .delete(SessionKey(userId, sessionId))
        .map(_ > 0)

    override def deleteAll(userId: UserId)(implicit executionContext: ExecutionContext): DBIO[Boolean] =
      dao
        .deleteAllFor(userId)
        .map(_ > 0)

    override def exists(userId: UserId, sessionId: SessionId)(implicit
        executionContext: ExecutionContext
    ): DBIO[Boolean] =
      dao.exists(SessionKey(userId, sessionId))

  }

}
