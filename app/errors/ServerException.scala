package errors

case class ServerException(error: ServerError) extends Throwable
