package graphql

import graphql.queries.{ DashboardQuery, ProjectQuery, UserQuery }
import graphql.types.user.UserId

trait Query extends UserQuery with ProjectQuery with DashboardQuery

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedInUserId: Option[UserId]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInUserId: Option[UserId] = _loggedInUserId
    }

}
