package graphql

import graphql.mutations.{ DashboardMutation, ProjectMutation, UserMutation }
import graphql.types.user.UserId

trait Mutation extends UserMutation with ProjectMutation with DashboardMutation

object Mutation {

  def apply(_graphQLServices: GraphQLServices, _loggedInUserId: Option[UserId]): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInUserId: Option[UserId] = _loggedInUserId
    }

}
