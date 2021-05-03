package services.project

import db.keys.UserId
import io.circe.generic.JsonCodec

@JsonCodec
case class UserRestriction(
    //An empty sequence is taken to mean "everyone".
    allowed: Seq[UserId],
    forbidden: Seq[UserId]
)

object UserRestriction {

  val empty: UserRestriction = UserRestriction(
    allowed = Seq.empty,
    forbidden = Seq.empty
  )

  def excluded(userIds: Seq[UserId]): UserRestriction =
    UserRestriction(
      allowed = Seq.empty,
      forbidden = userIds
    )

  def included(userIds: Seq[UserId]): UserRestriction =
    UserRestriction(
      allowed = userIds,
      forbidden = Seq.empty
    )

}
