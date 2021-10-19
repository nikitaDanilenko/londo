package graphql

import graphql.queries.{ DashboardQuery, ProjectQuery, UserQuery }
import security.jwt.JwtContent
trait Query extends UserQuery with ProjectQuery with DashboardQuery

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedInJwtContent: Option[JwtContent]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInJwtContent: Option[JwtContent] = _loggedInJwtContent
    }

}
