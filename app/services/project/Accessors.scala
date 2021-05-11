package services.project

import cats.data.NonEmptySet
import db.keys.UserId
import io.circe.generic.JsonCodec
import io.circe.{ Decoder, Encoder }

import scala.collection.immutable.SortedSet
import spire.compat._

sealed trait Accessors

object Accessors {

  case object Everyone extends Accessors

  case object Nobody extends Accessors

  case class EveryoneExcept(excluded: NonEmptySet[UserId]) extends Accessors

  case class NobodyExcept(included: NonEmptySet[UserId]) extends Accessors

  implicit val accessorsEncoder: Encoder[Accessors] = Encoder[Representation].contramap(toRepresentation)

  implicit val accessorsDecoder: Decoder[Accessors] = Decoder[Representation].map(fromRepresentation)

  def fromRepresentation(toRepresentation: Representation): Accessors = {
    val userIdsNonEmptySet = NonEmptySet.fromSet(SortedSet.from(toRepresentation.userIds))

    if (toRepresentation.isAllowList)
      userIdsNonEmptySet.fold(Everyone: Accessors)(NobodyExcept.apply)
    else
      userIdsNonEmptySet.fold(Nobody: Accessors)(EveryoneExcept.apply)
  }

  def toRepresentation(accessors: Accessors): Representation =
    accessors match {
      case Everyone                 => Representation.everyone
      case Nobody                   => Representation.nobody
      case EveryoneExcept(excluded) => Representation.everyoneExcept(excluded.toNonEmptyList.toList)
      case NobodyExcept(included)   => Representation.nobodyExcept(included.toNonEmptyList.toList)
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

  @JsonCodec
  case class Representation(
      isAllowList: Boolean,
      //An empty sequence is taken to mean "everyone".
      userIds: Seq[UserId]
  )

  object Representation {

    val nobody: Representation = Representation(
      isAllowList = false,
      userIds = Seq.empty
    )

    val everyone: Representation = Representation(
      isAllowList = true,
      userIds = Seq.empty
    )

    def everyoneExcept(excluded: Seq[UserId]): Representation =
      Representation(
        isAllowList = false,
        userIds = excluded
      )

    def nobodyExcept(included: Seq[UserId]): Representation =
      Representation(
        isAllowList = true,
        userIds = included
      )

  }

}
