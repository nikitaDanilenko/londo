package graphql

import security.jwt.LoggedIn

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
}

object GraphQLContext {

  def withoutUser(graphQLServices: GraphQLServices, configurations: Configurations): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _loggedIn = None,
      _configurations = configurations
    )

  def withUser(graphQLServices: GraphQLServices, loggedIn: LoggedIn, configurations: Configurations): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _loggedIn = Some(loggedIn),
      _configurations = configurations
    )

  private def create(
      graphQLServices: GraphQLServices,
      _loggedIn: Option[LoggedIn],
      _configurations: Configurations
  ): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices, _loggedIn, _configurations)
      override val query: Query       = Query(graphQLServices, _loggedIn)
    }

}
