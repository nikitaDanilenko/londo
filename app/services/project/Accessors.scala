package services.project

import cats.data.{ NonEmptyList, NonEmptySet }
import services.user.UserId

sealed trait Accessors

object Accessors {

  case object Everyone extends Accessors

  case object Nobody extends Accessors

  case class EveryoneExcept(excluded: NonEmptySet[UserId]) extends Accessors

  case class NobodyExcept(included: NonEmptySet[UserId]) extends Accessors

  def fromRepresentation(representation: Representation): Accessors = {
    val userIdSet = representation.userIds.map(nel => NonEmptySet.of(nel.head, nel.tail: _*))

    if (representation.isAllowList)
      userIdSet.fold(Everyone: Accessors)(NobodyExcept.apply)
    else
      userIdSet.fold(Nobody: Accessors)(EveryoneExcept.apply)
  }

  def toRepresentation(accessors: Accessors): Representation =
    accessors match {
      case Everyone                 => Representation.everyone
      case Nobody                   => Representation.nobody
      case EveryoneExcept(excluded) => Representation.everyoneExcept(excluded.toNonEmptyList)
      case NobodyExcept(included)   => Representation.nobodyExcept(included.toNonEmptyList)
    }

  def isRestricted(accessors: Accessors): Boolean =
    accessors match {
      case Everyone => false
      case _        => true
    }

  def allowUsers(accessors: Accessors, userIds: NonEmptySet[UserId]): Accessors =
    accessors match {
      case Everyone => Everyone
      case Nobody   => NobodyExcept(userIds)
      case EveryoneExcept(excluded) =>
        NonEmptySet
          .fromSet(excluded -- userIds)
          .fold(Everyone: Accessors)(EveryoneExcept.apply)
      case NobodyExcept(included) => NobodyExcept(included ++ userIds)
    }

  def blockUsers(accessors: Accessors, userIds: NonEmptySet[UserId]): Accessors =
    accessors match {
      case Everyone                 => EveryoneExcept(userIds)
      case Nobody                   => Nobody
      case EveryoneExcept(excluded) => EveryoneExcept(excluded ++ userIds)
      case NobodyExcept(included) =>
        NonEmptySet
          .fromSet(included -- userIds)
          .fold(Nobody: Accessors)(NobodyExcept.apply)
    }

  def hasAccess(userId: UserId, accessors: Accessors): Boolean =
    accessors match {
      case Everyone                 => true
      case Nobody                   => false
      case EveryoneExcept(excluded) => !excluded.contains(userId)
      case NobodyExcept(included)   => included.contains(userId)
    }

  case class Representation(
      isAllowList: Boolean,
      //An empty option is taken to mean "everyone".
      userIds: Option[NonEmptyList[UserId]]
  )

  object Representation {

    val nobody: Representation = Representation(
      isAllowList = false,
      userIds = None
    )

    val everyone: Representation = Representation(
      isAllowList = true,
      userIds = None
    )

    def everyoneExcept(excluded: NonEmptyList[UserId]): Representation =
      Representation(
        isAllowList = false,
        userIds = Some(excluded)
      )

    def nobodyExcept(included: NonEmptyList[UserId]): Representation =
      Representation(
        isAllowList = true,
        userIds = Some(included)
      )

  }

}
