package services.project

import io.circe.{ Decoder, Encoder }
import services.user.UserId

sealed trait Accessors

object Accessors {

  case object Everyone extends Accessors

  case object Nobody extends Accessors

  case class Restricted(users: Set[UserId]) extends Accessors

  implicit val accessorsEncoder: Encoder[Accessors] = Encoder[Option[Seq[UserId]]].contramap(toRepresentation)

  implicit val accessorsDecoder: Decoder[Accessors] = Decoder[Option[Seq[UserId]]].map(fromRepresentation)

  def fromRepresentation(users: Option[Seq[UserId]]): Accessors =
    users match {
      case Some(ids) =>
        if (ids.isEmpty)
          Nobody
        else Restricted(ids.toSet)
      case None => Everyone
    }

  def toRepresentation(accessors: Accessors): Option[Seq[UserId]] =
    accessors match {
      case Everyone          => None
      case Nobody            => Some(Seq.empty)
      case Restricted(users) => Some(users.toVector)
    }

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
