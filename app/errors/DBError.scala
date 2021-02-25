package errors

trait DBError extends Throwable

object DBError {
  case class EntityNotFound(message: String) extends DBError
}
