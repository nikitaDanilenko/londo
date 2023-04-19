package services

sealed abstract class DBError(errorMessage: String) extends Throwable(errorMessage) {}

object DBError {

  object Dashboard {
    case object NotFound extends DBError("No dashboard with the given id for the given user found")

    case object EntryNotFound extends DBError("No dashboard entry with the given id found")
  }

  object Project {
    case object NotFound extends DBError("No project with the given id for the given user found")

    case object TaskNotFound extends DBError("No project ingredient with the given id found")
  }

  object User {
    case object NotFound extends DBError("No user with the given id found")
  }

}
