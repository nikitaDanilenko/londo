package services.user

import cats.effect.IO

import java.util.UUID
import scala.util.Random

case class UserCreation(nickname: String, email: String, password: String)

object UserCreation {

  def create(userCreation: UserCreation): IO[User] = {
    for {
      id <- IO(UUID.randomUUID())
      salt <- IO(Random.nextString(40))
    } yield {
      User(
        id = UserId(id),
        nickname = userCreation.nickname,
        email = userCreation.email,
        passwordSalt = salt,
        passwordHash = "???"
      )
    }

  }

}
