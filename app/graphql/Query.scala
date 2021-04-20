package graphql

import db.keys.UserId
import graphql.queries.UserQuery

trait Query extends UserQuery

object Query {

  def apply(_graphQLServices: GraphQLServices, _loggedInUserId: Option[UserId]): Query =
    new Query {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInUserId: Option[UserId] = _loggedInUserId
    }

}
