package services.user

import cats.effect.IO
import db.UserId
import io.scalaland.chimney.dsl._
import security.Hash
import spire.math.Natural
import utils.random.RandomGenerator
import utils.transformer.implicits._

case class UserCreation(
    nickname: String,
    password: String,
    displayName: Option[String],
    email: String
)

object UserCreation {

  private val saltLength: Natural = Natural(40)

  def create(userCreation: UserCreation): IO[User] =
    for {
      id   <- RandomGenerator.randomUUID
      salt <- RandomGenerator.randomString(saltLength)
    } yield User(
      id = id.transformInto[UserId],
      nickname = userCreation.nickname,
      displayName = userCreation.displayName,
      email = userCreation.email,
      salt = salt,
      hash = Hash.fromPassword(
        userCreation.password,
        salt,
        Hash.defaultIterations
      )
    )

}
