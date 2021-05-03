package services.project

import cats.data.NonEmptySet
import db.keys.UserId
import io.circe.{ Decoder, Encoder }

import scala.collection.immutable.SortedSet
import spire.compat._

sealed trait Accessors

object Accessors {

  case object Everyone extends Accessors

  case object Nobody extends Accessors

  case class EveryoneExcept(excluded: NonEmptySet[UserId]) extends Accessors

  case class NobodyExcept(included: NonEmptySet[UserId]) extends Accessors

  case class Restricted(allowed: NonEmptySet[UserId], forbidden: NonEmptySet[UserId]) extends Accessors

  implicit val accessorsEncoder: Encoder[Accessors] = Encoder[Option[UserRestriction]].contramap(toRepresentation)

  implicit val accessorsDecoder: Decoder[Accessors] = Decoder[Option[UserRestriction]].map(fromRepresentation)

  def fromRepresentation(users: Option[UserRestriction]): Accessors = {
    def unsafeToNonEmptySet(userIds: Seq[UserId]): NonEmptySet[UserId] =
      NonEmptySet.fromSetUnsafe(SortedSet.from(userIds))

    users match {
      case Some(UserRestriction(allowed, forbidden)) =>
        if (allowed.isEmpty && forbidden.isEmpty)
          Nobody
        else if (allowed.isEmpty)
          EveryoneExcept(unsafeToNonEmptySet(forbidden))
        else if (forbidden.isEmpty)
          NobodyExcept(unsafeToNonEmptySet(allowed))
        else
          Restricted(
            allowed = unsafeToNonEmptySet(allowed),
            forbidden = unsafeToNonEmptySet(forbidden)
          )
      case None => Everyone
    }
  }

  def toRepresentation(accessors: Accessors): Option[UserRestriction] =
    accessors match {
      case Everyone                 => None
      case Nobody                   => Some(UserRestriction.empty)
      case EveryoneExcept(excluded) => Some(UserRestriction.excluded(excluded.toNonEmptyList.toList))
      case NobodyExcept(excluded)   => Some(UserRestriction.included(excluded.toNonEmptyList.toList))
      case Restricted(allowed, forbidden) =>
        Some(UserRestriction(allowed = allowed.toNonEmptyList.toList, forbidden = forbidden.toNonEmptyList.toList))
    }

  def isRestricted(accessors: Accessors): Boolean =
    accessors match {
      case Everyone => false
      case _        => true
    }

  def restricted(users: Set[UserId]): Accessors =
    if (users.isEmpty) Nobody else Restricted(users)

  def allowUser(accessors: Accessors, userId: UserId): Accessors =
    accessors match {
      case Everyone => Everyone
      case Nobody   => NobodyExcept(NonEmptySet.one(userId))
      case EveryoneExcept(excluded) =>
        NonEmptySet
          .fromSet(excluded - userId)
          .fold(Everyone: Accessors)(excludedWithoutUser =>
            Restricted(
              allowed = NonEmptySet.one(userId),
              forbidden = excludedWithoutUser
            )
          )
      case NobodyExcept(included) => NobodyExcept(included.add(userId))
      case Restricted(allowed, forbidden) =>
        val allowedWithUser = allowed.add(userId)
        NonEmptySet
          .fromSet(forbidden - userId)
          .fold(NobodyExcept(allowedWithUser): Accessors)(forbiddenWithoutUser =>
            Restricted(allowedWithUser, forbiddenWithoutUser)
          )
    }

  def forbidUser(accessors: Accessors, userId: UserId): Accessors =
    accessors match {
      case Everyone                 => EveryoneExcept(NonEmptySet.one(userId))
      case Nobody                   => Nobody
      case EveryoneExcept(excluded) => EveryoneExcept(excluded.add(userId))
      case NobodyExcept(included) =>
        NonEmptySet
          .fromSet(included - userId)
          .fold(Nobody: Accessors)(includedWithoutUser =>
            Restricted(
              allowed = includedWithoutUser,
              forbidden = NonEmptySet.one(userId)
            )
          )
      case Restricted(allowed, forbidden) =>
        val forbiddenWithUser = forbidden.add(userId)
        NonEmptySet
          .fromSet(allowed - userId)
          .fold(EveryoneExcept(forbiddenWithUser): Accessors)(allowedWithoutUser =>
            Restricted(
              allowed = allowedWithoutUser,
              forbidden = forbiddenWithUser
            )
          )
    }

}
