package graphql

import services.user.UserService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GraphQLServices @Inject() (val userService: UserService)(implicit val executionContext: ExecutionContext)
