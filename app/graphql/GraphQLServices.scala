package graphql

import services.project.ProjectService
import services.user.UserService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

case class GraphQLServices @Inject() (
    userService: UserService,
    projectService: ProjectService
)(implicit
    val executionContext: ExecutionContext
)
