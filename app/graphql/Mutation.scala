package graphql

import graphql.mutations.UserMutation
import services.user.UserId

trait Mutation extends UserMutation

object Mutation {

  def apply(_graphQLServices: GraphQLServices, _loggedInUserId: Option[UserId]): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInUserId: Option[UserId] = _loggedInUserId
    }

}
