package services.session

import db.daos.session.SessionKey
import db.generated.Tables
import db.{ SessionId, UserId }
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import security.jwt.JwtConfiguration
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import utils.transformer.implicits._

import java.sql.Date
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: SessionService.Companion
)(implicit
    executionContext: ExecutionContext
) extends SessionService
    with HasDatabaseConfigProvider[PostgresProfile] {

  override def add(userId: UserId, sessionId: SessionId, createdAt: Date): Future[SessionId] =
    db.run(companion.add(userId, sessionId, createdAt))

  override def delete(userId: UserId, sessionId: SessionId): Future[Boolean] =
    db.run(companion.delete(userId, sessionId))

  override def deleteAll(userId: UserId): Future[Boolean] =
    db.run(companion.deleteAll(userId))

  override def exists(userId: UserId, sessionId: SessionId): Future[Boolean] =
    db.run(companion.exists(userId, sessionId))

}

object Live {

  class Companion @Inject() (
      dao: db.daos.session.DAO
  ) extends SessionService.Companion {

    private val jwtConfiguration: JwtConfiguration = JwtConfiguration.default

    private val allowedValidityInDays: Int =
      Math.ceil(jwtConfiguration.restrictedDurationInSeconds.toDouble / 86400).toInt

    override def add(userId: UserId, sessionId: SessionId, createdAt: java.sql.Date)(implicit
        executionContext: ExecutionContext
    ): DBIO[SessionId] = {
      dao
        .deleteAllBefore(
          userId,
          createdAt.toLocalDate
            .minus(allowedValidityInDays, ChronoUnit.DAYS)
            .transformInto[Date]
        )
        .andThen(
          dao
            .insert(
              Tables.SessionRow(
                id = sessionId.transformInto[UUID],
                userId = userId.transformInto[UUID],
                createdAt = createdAt
              )
            )
            .map(_.id.transformInto[SessionId])
        )
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
