package graphql

import security.jwt.LoggedIn
trait Query extends graphql.queries.user.Query with graphql.queries.project.Query with graphql.queries.dashboard.Query

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn]       = _loggedIn
    }

}
