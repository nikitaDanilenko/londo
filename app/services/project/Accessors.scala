package services.project

import io.circe.generic.JsonCodec
import services.user.UserId

@JsonCodec
sealed trait Accessors

object Accessors {
  case object Everyone extends Accessors
  case object Nobody extends Accessors
  case class Restricted(users: Set[UserId]) extends Accessors

  def isRestricted(accessors: Accessors): Boolean =
    accessors match {
      case Everyone => false
      case _        => true
    }

  def userIdsOf(accessors: Accessors): Set[UserId] =
    accessors match {
      case Restricted(users) => users
      case _                 => Set.empty
    }

}
