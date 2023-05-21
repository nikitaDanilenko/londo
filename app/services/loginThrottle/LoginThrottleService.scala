package services.loginThrottle

import db.UserId
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait LoginThrottleService {

  def get(userId: UserId): Future[Option[LoginThrottle]]

  def create(userId: UserId): Future[ServerError.Or[LoginThrottle]]

  def update(userId: UserId, update: Update): Future[ServerError.Or[LoginThrottle]]

}

object LoginThrottleService {

  trait Companion {
    def get(userId: UserId)(implicit ec: ExecutionContext): DBIO[Option[LoginThrottle]]

    def create(
        userId: UserId
    )(implicit ec: ExecutionContext): DBIO[LoginThrottle]

    def update(
        userId: UserId,
        update: Update
    )(implicit ec: ExecutionContext): DBIO[LoginThrottle]

  }

}
