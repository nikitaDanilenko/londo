package services.user

import cats.data.OptionT
import db.UserId
import db.generated.Tables
import io.scalaland.chimney.dsl._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import security.Hash
import security.jwt.JwtConfiguration
import services.DBError
import services.common.Transactionally.syntax._
import services.session.SessionService
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile
import utils.DBIOUtil.instances._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class Live @Inject() (
    override protected val dbConfigProvider: DatabaseConfigProvider,
    companion: UserService.Companion
)(implicit
    executionContext: ExecutionContext
) extends UserService
    with HasDatabaseConfigProvider[PostgresProfile] {
  override def get(userId: UserId): Future[Option[User]] = db.runTransactionally(companion.get(userId))

  override def getByNickname(nickname: String): Future[Option[User]] =
    db.runTransactionally(companion.getByNickname(nickname))

  override def getByIdentifier(string: String): Future[Seq[User]] =
    db.runTransactionally(companion.getByIdentifier(string))

  override def add(user: User): Future[Boolean] =
    db.runTransactionally(companion.add(user))
      .map(_ => true)
      .recover { case _ => false }

  override def update(userId: UserId, userUpdate: Update): Future[User] =
    db.runTransactionally(companion.update(userId, userUpdate))

  override def updatePassword(userId: UserId, password: String): Future[Boolean] =
    db.runTransactionally(companion.updatePassword(userId, password))

  override def delete(userId: UserId): Future[Boolean] = db.runTransactionally(companion.delete(userId))

}

object Live {

  class Companion @Inject() (
      userDao: db.daos.user.DAO
  ) extends UserService.Companion {

    def get(userId: UserId)(implicit executionContext: ExecutionContext): DBIO[Option[User]] =
      OptionT(userDao.find(userId))
        .map(_.transformInto[User])
        .value

    override def getByNickname(nickname: String)(implicit executionContext: ExecutionContext): DBIO[Option[User]] =
      OptionT
        .liftF(userDao.findByNickname(nickname))
        .subflatMap(_.headOption)
        .map(_.transformInto[User])
        .value

    override def getByIdentifier(string: String)(implicit executionContext: ExecutionContext): DBIO[Seq[User]] =
      userDao
        .findByIdentifier(string)
        .map(_.map(_.transformInto[User]))

    override def add(user: User)(implicit executionContext: ExecutionContext): DBIO[Unit] =
      userDao
        .insert(user.transformInto[Tables.UserRow])
        .map(_ => ())

    override def update(userId: UserId, userUpdate: Update)(implicit
                                                            executionContext: ExecutionContext
    ): DBIO[User] = {
      val findAction = OptionT(get(userId))
        .getOrElseF(DBIO.failed(DBError.User.NotFound))

      for {
        user <- findAction
        _ <- userDao.update(
          Update
            .update(user, userUpdate)
            .transformInto[Tables.UserRow]
        )
        updatedUser <- findAction
      } yield updatedUser
    }

    override def updatePassword(userId: UserId, password: String)(implicit
        executionContext: ExecutionContext
    ): DBIO[Boolean] = {
      val transformer = for {
        user <- OptionT(get(userId))
        newHash = Hash.fromPassword(
          password,
          user.salt,
          Hash.defaultIterations
        )
        newUser = user.copy(hash = newHash)
        result <- OptionT.liftF(
          userDao.update(newUser.transformInto[Tables.UserRow])
        )
      } yield result

      transformer.getOrElseF(DBIO.failed(DBError.User.NotFound))
    }

    override def delete(userId: UserId)(implicit executionContext: ExecutionContext): DBIO[Boolean] =
      userDao
        .delete(userId)
        .map(_ > 0)

  }

}
