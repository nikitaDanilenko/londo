package services.user

import cats.effect.IO
import io.circe.generic.JsonCodec
import security.Hash
import utils.random.RandomGenerator

@JsonCodec
case class UserCreation(
    nickname: String,
    email: String,
    password: String
)

object UserCreation {

  val saltLength: Int = 40
  val kdfIterations: Int = 120000

  def create(userCreation: UserCreation): IO[CreatedUser] =
    for {
      id <- RandomGenerator.randomUUID
      salt <- RandomGenerator.randomString(saltLength)
    } yield CreatedUser(
      User(
        id = UserId(id),
        nickname = userCreation.nickname,
        email = userCreation.email,
        settings = UserSettings.default,
        details = UserDetails.default
      ),
      passwordParameters = PasswordParameters(
        salt = salt,
        hash = Hash.fromPassword(
          password = userCreation.password,
          salt = salt,
          iterations = kdfIterations
        ),
        iterations = kdfIterations
      )
    )

}
