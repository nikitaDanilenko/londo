package services

sealed abstract class DBError(errorMessage: String) extends Throwable(errorMessage) {}

object DBError {

  object Dashboard {
    case object NotFound extends DBError("No dashboard with the given id for the given user found")

  }

  object Project {
    case object NotFound extends DBError("No project with the given id for the given user found")

    case object TaskNotFound extends DBError("No task with the given id found")
  }

  object User {
    case object NotFound extends DBError("No user with the given id found")
  }

  object Login {
    case object NotFound extends DBError("No login for user with the given id found")
  }

  object Simulation {
    case object NotFound extends DBError("No simulation with the given id found")
  }

}
