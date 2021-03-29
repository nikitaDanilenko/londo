package controllers.graphql

import errors.ServerException
import sangria.execution.{ ExceptionHandler, HandledException }

object GraphQLExceptionHandler {

  val exceptionHandler: ExceptionHandler = new ExceptionHandler(
    onException = {
      case (_, serverException: ServerException) =>
        HandledException.single(message = serverException.error.message)
    }
  )

}
