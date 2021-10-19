package graphql

import graphql.mutations.{ DashboardMutation, ProjectMutation, UserMutation }
import security.jwt.JwtContent

trait Mutation extends UserMutation with ProjectMutation with DashboardMutation

object Mutation {

  def apply(_graphQLServices: GraphQLServices, _loggedInJwtContent: Option[JwtContent]): Mutation =
    new Mutation {
      override protected val graphQLServices: GraphQLServices = _graphQLServices
      override protected val loggedInJwtContent: Option[JwtContent] = _loggedInJwtContent
    }

}
