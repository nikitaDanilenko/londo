package graphql

import graphql.mutations.UserMutation

trait Mutation extends UserMutation

object Mutation {

  def apply(graphQLServices: GraphQLServices): Mutation = {
    val services = graphQLServices
    new Mutation {
      override protected val graphQLServices: GraphQLServices = services
    }
  }

}
