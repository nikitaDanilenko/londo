package graphql

import errors.{ BulkServerException, ServerError, ServerException }

import scala.concurrent.{ ExecutionContext, Future }

trait HasGraphQLServices {
  protected def graphQLServices: GraphQLServices

  protected implicit lazy val executionContext: ExecutionContext = graphQLServices.executionContext

}

object HasGraphQLServices {

  object syntax {

    implicit class ToServerException[A](val future: Future[ServerError.Or[A]]) extends AnyVal {

      def handleServerError(implicit executionContext: ExecutionContext): Future[A] =
        future.flatMap(
          _.fold(
            serverError => Future.failed(ServerException(serverError)),
            Future.successful
          )
        )

    }

  }

}
