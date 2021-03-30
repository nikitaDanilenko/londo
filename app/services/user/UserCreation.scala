package services.user

import cats.effect.IO
import io.circe.generic.JsonCodec
import security.Hash
import spire.math.Natural
import utils.random.RandomGenerator

@JsonCodec
case class UserCreation(
    nickname: String,
    email: String,
    password: String,
    token: String
)

object UserCreation {

  val saltLength: Natural = Natural(40)
  val kdfIterations: Natural = Natural(120000)

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
