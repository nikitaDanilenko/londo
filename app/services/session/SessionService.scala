package services.session

import db.{ SessionId, UserId }
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait SessionService {

  def create(userId: UserId): Future[ServerError.Or[Session]]

  def delete(userId: UserId, sessionId: SessionId): Future[ServerError.Or[Boolean]]

  def deleteAll(userId: UserId): Future[ServerError.Or[Boolean]]

  def exists(userId: UserId, sessionId: SessionId): Future[Boolean]

}

object SessionService {

  trait Companion {

    def create(userId: UserId)(implicit ec: ExecutionContext): DBIO[Session]

    def delete(userId: UserId, sessionId: SessionId)(implicit ec: ExecutionContext): DBIO[Boolean]

    def deleteAll(userId: UserId)(implicit ec: ExecutionContext): DBIO[Boolean]

    def exists(userId: UserId, sessionId: SessionId)(implicit ec: ExecutionContext): DBIO[Boolean]
  }

}
