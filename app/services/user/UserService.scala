package services.user

import db.UserId
import errors.ServerError
import slick.dbio.DBIO

import scala.concurrent.{ ExecutionContext, Future }

trait UserService {
  def get(userId: UserId): Future[Option[User]]
  def getByNickname(nickname: String): Future[Option[User]]

  def getByIdentifier(string: String): Future[Seq[User]]

  def add(user: User): Future[ServerError.Or[User]]

  def update(userId: UserId, userUpdate: Update): Future[ServerError.Or[User]]

  def updatePassword(userId: UserId, password: String): Future[ServerError.Or[Boolean]]
  def delete(userId: UserId): Future[ServerError.Or[Boolean]]
}

object UserService {

  trait Companion {
    def get(userId: UserId)(implicit executionContext: ExecutionContext): DBIO[Option[User]]
    def getByNickname(nickname: String)(implicit executionContext: ExecutionContext): DBIO[Option[User]]
    def getByIdentifier(string: String)(implicit executionContext: ExecutionContext): DBIO[Seq[User]]
    def add(user: User)(implicit executionContext: ExecutionContext): DBIO[User]
    def update(userId: UserId, userUpdate: Update)(implicit executionContext: ExecutionContext): DBIO[User]
    def updatePassword(userId: UserId, password: String)(implicit executionContext: ExecutionContext): DBIO[Boolean]
    def delete(userId: UserId)(implicit executionContext: ExecutionContext): DBIO[Boolean]

  }

}
