package graphql

import services.user.UserId

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
}

object GraphQLContext {

  def withoutUser(graphQLServices: GraphQLServices): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _userId = None
    )

  def withUser(graphQLServices: GraphQLServices, userId: UserId): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _userId = Some(userId)
    )

  private def create(graphQLServices: GraphQLServices, _userId: Option[UserId]): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices, _userId)
      override val query: Query = Query(graphQLServices, _userId)
    }

}
