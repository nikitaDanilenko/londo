package errors

sealed trait ServerError {
  def message: String
}

object ServerError {

  private abstract class ServerErrorInstance(override val message: String) extends ServerError

  object Login {
    case object Failure extends ServerErrorInstance("Invalid combination of user name and password.")
  }

}
