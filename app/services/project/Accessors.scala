package services.project

import services.user.UserId

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

  def restricted(users: Set[UserId]): Accessors =
    if (users.isEmpty) Nobody else Restricted(users)

}
