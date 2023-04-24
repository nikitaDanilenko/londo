package graphql

import graphql.queries.{ DashboardQuery, ProjectQuery }
import security.jwt.LoggedIn
trait Query extends graphql.queries.user.Query with ProjectQuery with DashboardQuery

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn]       = _loggedIn
    }

}
