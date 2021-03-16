package graphql

import cats.effect.IO

trait HasGraphQLServices {
  protected def graphQLServices: GraphQLServices

  protected implicit lazy val ioImplicits: IOImplicits[IO] =
    IOImplicits.fromExecutionContext(graphQLServices.executionContext)

}
