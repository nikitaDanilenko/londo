package services.user

import io.scalaland.chimney.dsl._

case class Update(
    displayName: Option[String]
)

object Update {

  def update(user: User, update: Update): User =
    user.patchUsing(update)

}
