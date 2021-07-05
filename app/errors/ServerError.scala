package errors

import cats.data.{ EitherT, NonEmptyList, Validated, ValidatedNel }
import doobie.ConnectionIO
import io.circe.{ Encoder, Json }
import io.circe.syntax._

sealed trait ServerError {
  def message: String
}

object ServerError {

  type Or[A] = Either[ServerError, A]
  type Valid[A] = ValidatedNel[ServerError, A]

  def fromEmpty[A](option: Option[A], ifNonEmpty: => ServerError): Valid[Unit] =
    Validated.fromEither {
      option match {
        case Some(_) => Left(NonEmptyList.of(ifNonEmpty))
        case None    => Right(())
      }
    }

  def fromEither[A](either: Or[A]): Valid[A] =
    Validated.fromEither[NonEmptyList[ServerError], A](either.left.map(NonEmptyList.of(_)))

  def fromEitherNel[A](either: Either[NonEmptyList[ServerError], A]): Valid[A] =
    Validated.fromEither(either)

  def fromCondition[A](condition: Boolean, errorCase: => ServerError, successCase: => A): Valid[A] =
    Validated.condNel(condition, successCase, errorCase)

  def fromOption[A](option: Option[A], errorCase: => ServerError): Valid[A] =
    fromEither(option.toRight(errorCase))

  def liftC[A](ca: ConnectionIO[A]): EitherT[ConnectionIO, NonEmptyList[ServerError], A] =
    EitherT.liftF[ConnectionIO, NonEmptyList[ServerError], A](ca)

  implicit val serverErrorEncoder: Encoder[ServerError] = Encoder.instance[ServerError] { serverError =>
    Json.obj(
      "message" -> serverError.message.asJson
    )
  }

  sealed abstract class ServerErrorInstance(override val message: String) extends ServerError

  object Login {
    case object Failure extends ServerErrorInstance("Invalid combination of user name and password.")
  }

  object Authentication {

    object Token {
      case object Decoding extends ServerErrorInstance("Error decoding JWT: Format or signature is wrong")
      case object Content extends ServerErrorInstance("Error parsing JWT content: Unexpected format")
      case object Missing extends ServerErrorInstance("Missing JWT")
      case object Registration extends ServerErrorInstance("Missing or wrong registration token")
      case object MissingSessionKey extends ServerErrorInstance("Unknown session key reference")
      case object Restricted extends ServerErrorInstance("Invalid access rights")
    }

    object Signature {
      case object MissingInstant extends ServerErrorInstance("Missing instant of signature creation")
      case object Missing extends ServerErrorInstance("Missing signature")
      case object Invalid extends ServerErrorInstance("Invalid signature")
    }

  }

  object User {
    case object NotFound extends ServerErrorInstance("No user with the given id found")
    case object SettingsNotFound extends ServerErrorInstance("No user settings for the given user found")
    case object DetailsNotFound extends ServerErrorInstance("No user details for the given user found")
  }

  object Registration {
    case object EmailAlreadyRegistered extends ServerErrorInstance("The given email is already in use")

    case object NoRegistrationTokenForEmail
        extends ServerErrorInstance("No registration token for given email address found")

  }

  object Task {

    object Plain {
      case object EmptyName extends ServerErrorInstance("Empty name in plain task")
      case object EmptyKind extends ServerErrorInstance("Empty kind in plain task")
      case object EmptyReached extends ServerErrorInstance("Empty reached value in plain task")
      case object EmptyReachable extends ServerErrorInstance("Empty reachable value in plain task")
      case object NonEmptyProjectReference extends ServerErrorInstance("Non-empty project reference in plain task")
    }

    object ProjectReference {
      case object NonEmptyName extends ServerErrorInstance("Non-empty name in project reference task")
      case object NonEmptyKind extends ServerErrorInstance("Non-empty kind in project reference task")
      case object NonEmptyReached extends ServerErrorInstance("Non-empty reached value in project reference task")
      case object NonEmptyReachable extends ServerErrorInstance("Non-empty reachable value in project reference task")
      case object NonEmptyUnit extends ServerErrorInstance("Non-empty unit in project reference task")
      case object EmptyProjectReference extends ServerErrorInstance("Empty project reference in project reference task")
    }

    case object NegativeWeight extends ServerErrorInstance("Negative weight")
    case object NotFound extends ServerErrorInstance("No task with the given id found")

  }

  object Project {
    case object NotFound extends ServerErrorInstance("No project with the given id found")
  }

}
