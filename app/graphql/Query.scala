package graphql

import graphql.queries.{ DashboardQuery, ProjectQuery, UserQuery }
import security.jwt.LoggedIn
trait Query extends UserQuery with ProjectQuery with DashboardQuery

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn] = _loggedIn
    }

}
