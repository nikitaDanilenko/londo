package graphql

import graphql.queries.UserQuery

trait Query extends UserQuery

object Query {

  def apply(graphQLServices: GraphQLServices): Query = {
    val services = graphQLServices
    new Query {
      override protected def graphQLServices: GraphQLServices = services
    }
  }

}
