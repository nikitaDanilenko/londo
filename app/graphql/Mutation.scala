package graphql

import graphql.mutations.{ DashboardMutation, ProjectMutation, UserMutation }
import security.jwt.LoggedIn

trait Mutation extends UserMutation with ProjectMutation with DashboardMutation

object Mutation {

  def apply(_graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn]       = _loggedIn
    }

}
