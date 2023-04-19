package services.user

import io.scalaland.chimney.dsl._

case class UserUpdate(
    displayName: Option[String],
    email: String
)

object UserUpdate {

  def update(user: User, userUpdate: UserUpdate): User =
    user.patchUsing(userUpdate)

}
