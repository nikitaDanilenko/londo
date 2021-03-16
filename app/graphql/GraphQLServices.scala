package graphql

import services.user.UserDTO

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class GraphQLServices @Inject() (val userDTO: UserDTO)(implicit val executionContext: ExecutionContext)
