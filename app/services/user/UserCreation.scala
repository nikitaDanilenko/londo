package services.user

import cats.effect.IO
import io.circe.generic.JsonCodec
import security.Hash

import java.util.UUID
import scala.util.Random

@JsonCodec
case class UserCreation(
    nickname: String,
    email: String,
    password: String
)

object UserCreation {

  val saltLength: Int = 40

  def create(userCreation: UserCreation): IO[User] =
    for {
      id <- IO(UUID.randomUUID())
      salt <- IO(Random.nextString(saltLength))
    } yield User(
      id = UserId(id),
      nickname = userCreation.nickname,
      email = userCreation.email,
      passwordSalt = salt,
      passwordHash = Hash.fromPassword(
        password = userCreation.password,
        salt = salt
      ),
      settings = UserSettings.default,
      details = UserDetails.default
    )

}
