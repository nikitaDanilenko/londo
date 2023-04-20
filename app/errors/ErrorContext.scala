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
    case object NotFound extends ServerErrorInstance("No user with the given id found")
    case object Delete   extends ServerErrorInstance("Error while deleting a user")
    case object Replace  extends ServerErrorInstance("Error while replacing a user")
    case object Create   extends ServerErrorInstance("Error while creating a user")

    object Settings {
      case object NotFound extends ServerErrorInstance("No user settings for the given user found")
      case object Delete   extends ServerErrorInstance("Error deleting user settings")
      case object Replace  extends ServerErrorInstance("Error replacing user settings")
      case object Create   extends ServerErrorInstance("Error creating user settings")
    }

    object Details {
      case object NotFound extends ServerErrorInstance("No user details for the given user found")
      case object Delete   extends ServerErrorInstance("Error deleting user details")
      case object Replace  extends ServerErrorInstance("Error replacing user details")
      case object Create   extends ServerErrorInstance("Error creating user details")
    }

  }

  object Registration {
    case object EmailAlreadyRegistered extends ServerErrorInstance("The given email is already in use")

    case object NoRegistrationTokenForEmail
        extends ServerErrorInstance("No registration token for given email address found")

    case object Delete  extends ServerErrorInstance("Error while deleting registration token")
    case object Replace extends ServerErrorInstance("Error while replacing registration token")
  }

  object Task {

    object Plain {

      case class Create(errorMessage: String)
          extends ServerErrorInstance(s"Error while creating a plain task: $errorMessage")

      case class Update(errorMessage: String)
          extends ServerErrorInstance(s"Error while replacing a plain task: $errorMessage")

      case class Delete(errorMessage: String)
          extends ServerErrorInstance(s"Error while deleting a plain task: $errorMessage")

    }

    object Reference {

      case class Create(errorMessage: String)
          extends ServerErrorInstance(s"Error while creating a reference task: $errorMessage")

      case class Delete(errorMessage: String)
          extends ServerErrorInstance(s"Error while deleting a reference task: $errorMessage")

    }

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
    case object NotFound           extends ServerErrorInstance("No dashboard with the given id found")
    case object NoReadAccess       extends ServerErrorInstance("No read access for dashboard")
    case object NoWriteAccess      extends ServerErrorInstance("No write access for dashboard")
    case object AccessDbError      extends ServerErrorInstance("Error writing dashboard access")
    case object AccessEntryDbError extends ServerErrorInstance("Error writing dashboard access")
    case object Delete             extends ServerErrorInstance("Error while deleting a dashboard")
    case object Replace            extends ServerErrorInstance("Error while replacing a dashboard")
    case object Create             extends ServerErrorInstance("Error while creating a dashboard")
  }

}
