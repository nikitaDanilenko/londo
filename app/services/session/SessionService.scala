package services.session

import db.{ SessionId, UserId }
import slick.dbio.DBIO

import java.sql.Date
import scala.concurrent.{ ExecutionContext, Future }

trait SessionService {

  def add(
      userId: UserId,
      sessionId: SessionId,
      createdAt: Date
  ): Future[SessionId]

  def delete(userId: UserId, sessionId: SessionId): Future[Boolean]

  def deleteAll(userId: UserId): Future[Boolean]

  def exists(userId: UserId, sessionId: SessionId): Future[Boolean]

}

object SessionService {

  trait Companion {

    def add(
        userId: UserId,
        sessionId: SessionId,
        createdAt: Date
    )(implicit
        executionContext: ExecutionContext
    ): DBIO[SessionId]

    def delete(userId: UserId, sessionId: SessionId)(implicit ec: ExecutionContext): DBIO[Boolean]

    def deleteAll(userId: UserId)(implicit ec: ExecutionContext): DBIO[Boolean]

    def exists(userId: UserId, sessionId: SessionId)(implicit ec: ExecutionContext): DBIO[Boolean]
  }

}
