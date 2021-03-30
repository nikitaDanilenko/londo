package errors

sealed trait ServerError {
  def message: String
}

object ServerError {

  type Or[A] = Either[ServerError, A]

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
    }

  }

  object User {
    case object NotFound extends ServerErrorInstance("No user with the given id found")
  }

}
