package graphql

import security.jwt.LoggedIn

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
}

object GraphQLContext {

  def withoutUser(graphQLServices: GraphQLServices): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _loggedIn = None
    )

  def withUser(graphQLServices: GraphQLServices, loggedIn: LoggedIn): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _loggedIn = Some(loggedIn)
    )

  private def create(graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices, _loggedIn)
      override val query: Query       = Query(graphQLServices, _loggedIn)
    }

}
