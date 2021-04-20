package graphql.mutations

import graphql.{ HasGraphQLServices, HasLoggedInUser }

trait ProjectMutation extends HasGraphQLServices with HasLoggedInUser {
  import ioImplicits._

  def createProject(
      name: String,
      description: String
  )

}
