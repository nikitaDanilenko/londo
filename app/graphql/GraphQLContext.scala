package graphql

import security.jwt.JwtContent

sealed trait GraphQLContext {
  def mutation: Mutation
  def query: Query
}

object GraphQLContext {

  def withoutUser(graphQLServices: GraphQLServices): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _jwtContent = None
    )

  def withUser(graphQLServices: GraphQLServices, jwtContent: JwtContent): GraphQLContext =
    create(
      graphQLServices = graphQLServices,
      _jwtContent = Some(jwtContent)
    )

  private def create(graphQLServices: GraphQLServices, _jwtContent: Option[JwtContent]): GraphQLContext =
    new GraphQLContext {
      override val mutation: Mutation = Mutation(graphQLServices, _jwtContent)
      override val query: Query = Query(graphQLServices, _jwtContent)
    }

}
