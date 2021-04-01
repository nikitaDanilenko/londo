package errors

import io.circe.{ Encoder, Json }
import io.circe.syntax._

sealed trait ServerError {
  def message: String
}

object ServerError {

  type Or[A] = Either[ServerError, A]

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

    case object MissingAuthenticationInstant extends ServerErrorInstance("Missing instant of signature creation")
    case object MissingAuthentication extends ServerErrorInstance("Missing signature")

  }

  object User {
    case object NotFound extends ServerErrorInstance("No user with the given id found")
  }

}
