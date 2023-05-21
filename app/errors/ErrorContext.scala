package errors

sealed trait ErrorContext {
  def message: String
}

object ErrorContext {

  sealed abstract class ServerErrorInstance(override val message: String) extends ErrorContext

  implicit class ErrorContextToServerError(val errorContext: ErrorContext) extends AnyVal {
    def asServerError: ServerError = ServerError.fromContext(errorContext)
  }

  object Login {
    case object Failure extends ServerErrorInstance("Invalid combination of user name and password.")
    case class Create(errorMessage: String) extends ServerErrorInstance(s"Error attempting to login: $errorMessage")

    case class Update(errorMessage: String)
        extends ServerErrorInstance(s"Error attempting to update login: $errorMessage")

    case class Limit(errorMessage: String) extends ServerErrorInstance(s"Too many failed logins: $errorMessage")
  }

  object Authentication {

    object Token {
      case object Decoding          extends ServerErrorInstance("Error decoding JWT: Format or signature is wrong")
      case object Content           extends ServerErrorInstance("Error parsing JWT content: Unexpected format")
      case object Missing           extends ServerErrorInstance("Missing JWT")
      case object Registration      extends ServerErrorInstance("Missing or wrong registration token")
      case object MissingSessionKey extends ServerErrorInstance("Unknown session key reference")
      case object Restricted        extends ServerErrorInstance("Invalid access rights")
    }

  }

  object User {
    case object NotFound                    extends ServerErrorInstance("No user with the given id found")
    case class Delete(errorMessage: String) extends ServerErrorInstance(s"Error while deleting a user: $errorMessage")
    case class Update(errorMessage: String) extends ServerErrorInstance(s"Error while updating a user: $errorMessage")
    case class Create(errorMessage: String) extends ServerErrorInstance(s"Error while creating a user: $errorMessage")
    case object Exists                      extends ServerErrorInstance("This nickname is already taken")

  }

  object Registration {
    case object EmailAlreadyRegistered extends ServerErrorInstance("The given email is already in use")

    case object NoRegistrationTokenForEmail
        extends ServerErrorInstance("No registration token for given email address found")

    case object Delete  extends ServerErrorInstance("Error while deleting registration token")
    case object Replace extends ServerErrorInstance("Error while replacing registration token")
  }

  object Task {

    case class Create(errorMessage: String) extends ServerErrorInstance(s"Error while creating a task: $errorMessage")

    case class Update(errorMessage: String) extends ServerErrorInstance(s"Error while replacing a task: $errorMessage")

    case class Delete(errorMessage: String) extends ServerErrorInstance(s"Error while deleting a task: $errorMessage")

    case object NotFound extends ServerErrorInstance("No task with the given id found")

  }

  object Project {
    case object NotFound extends ServerErrorInstance("No project with the given id found")

    case class Delete(
        errorMessage: String
    ) extends ServerErrorInstance(s"Error while deleting a project: $errorMessage")

    case class Update(
        errorMessage: String
    ) extends ServerErrorInstance(s"Error while updating a project: $errorMessage")

    case class Create(
        errorMessage: String
    ) extends ServerErrorInstance(s"Error while creating a project: $errorMessage")

  }

  object Conversion {
    case object IntToNatural    extends ServerErrorInstance("The integer does not represent a valid natural number")
    case object BigIntToNatural extends ServerErrorInstance("The integer does not represent a valid natural number")
    case object PositiveNatural extends ServerErrorInstance("Zero is not a positive natural number")
  }

  object Dashboard {
    case object NotFound extends ServerErrorInstance("No dashboard with the given id found")

    case class Delete(errorMessage: String)
        extends ServerErrorInstance(s"Error while deleting a dashboard: $errorMessage")

    case class Update(errorMessage: String)
        extends ServerErrorInstance(s"Error while updating a dashboard: $errorMessage")

    case class Create(errorMessage: String)
        extends ServerErrorInstance(s"Error while creating a dashboard: $errorMessage")

  }

  object DashboardEntry {
    case object NotFound extends ServerErrorInstance("No dashboard with the given key found")

    case class Delete(errorMessage: String)
        extends ServerErrorInstance(s"Error while deleting a dashboard entry: $errorMessage")

    case class Create(errorMessage: String)
        extends ServerErrorInstance(s"Error while creating a dashboard entry: $errorMessage")

  }

  object Session {

    case class Delete(errorMessage: String)
        extends ServerErrorInstance(s"Error while deleting a dashboard entry: $errorMessage")

    case class Create(errorMessage: String)
        extends ServerErrorInstance(s"Error while creating a dashboard entry: $errorMessage")

    case object NotFound extends ServerErrorInstance("User session not found.")
  }

  object Mail {
    case object SendingFailed extends ServerErrorInstance("Sending of message failed")
  }

}
