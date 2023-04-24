package graphql

import graphql.mutations.{ DashboardMutation, ProjectMutation }
import security.jwt.LoggedIn

trait Mutation extends graphql.mutations.user.Mutation with ProjectMutation with DashboardMutation

object Mutation {

  def apply(_graphQLServices: GraphQLServices, _loggedIn: Option[LoggedIn]): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedIn: Option[LoggedIn]       = _loggedIn
    }

}
