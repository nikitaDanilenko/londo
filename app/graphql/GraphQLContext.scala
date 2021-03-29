package graphql

import java.util.UUID

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
  def userId: Option[UUID]
}

object GraphQLContext {

  def withoutUser(graphQLServices: GraphQLServices): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _userId = None
    )

  def withUser(graphQLServices: GraphQLServices, userId: UUID): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _userId = Some(userId)
    )

  private def create(graphQLServices: GraphQLServices, _userId: Option[UUID]): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices)
      override val query: Query = Query(graphQLServices)
      override val userId: Option[UUID] = _userId
    }

}
