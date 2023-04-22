package services.loginAttempt

import db.UserId
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait LoginAttemptService {

  def get(userId: UserId): Future[Option[LoginAttempt]]

  def create(userId: UserId): Future[ServerError.Or[LoginAttempt]]

  def update(userId: UserId, update: Update): Future[ServerError.Or[LoginAttempt]]

}

object LoginAttemptService {

  trait Companion {
    def get(userId: UserId)(implicit ec: ExecutionContext): DBIO[Option[LoginAttempt]]

    def create(
        userId: UserId
    )(implicit ec: ExecutionContext): DBIO[LoginAttempt]

    def update(
        userId: UserId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[LoginAttempt]

  }

}
