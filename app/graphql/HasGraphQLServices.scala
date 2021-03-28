package graphql

import cats.effect.IO

import scala.concurrent.ExecutionContext

trait HasGraphQLServices {
  protected def graphQLServices: GraphQLServices

  protected implicit lazy val ioImplicits: IOImplicits[IO] =
    IOImplicits.fromExecutionContext(graphQLServices.executionContext)

  protected implicit lazy val executionContext: ExecutionContext = graphQLServices.executionContext

}
