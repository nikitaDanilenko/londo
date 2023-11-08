package graphql

import security.jwt.LoggedIn

trait Mutation
    extends graphql.mutations.user.Mutation
    with graphql.mutations.project.Mutation
    with graphql.mutations.dashboard.Mutation

object Mutation {

  def apply(
      _graphQLServices: GraphQLServices,
      _loggedIn: Option[LoggedIn],
      _configurations: Configurations
  ): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn]       = _loggedIn
      override protected val configurations: Configurations   = _configurations
    }

}
