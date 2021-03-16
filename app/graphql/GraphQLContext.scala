package graphql

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
}

object GraphQLContext {

  def apply(graphQLServices: GraphQLServices): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices)
      override val query: Query = Query(graphQLServices)
    }

}
