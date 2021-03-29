package graphql

import graphql.queries.UserQuery

trait Query extends UserQuery

object Query {

  def apply(graphQLServices: GraphQLServices): Query = {
    val services = graphQLServices
    new Query {
      override protected val graphQLServices: GraphQLServices = services
    }
  }

}
