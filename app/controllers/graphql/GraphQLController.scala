package controllers.graphql

import errors.ServerError
import graphql._
import io.circe.Json
import io.circe.syntax._
import play.api.Logging
import play.api.libs.circe.Circe
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents }
import sangria.execution.Executor
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import utils.jwt.{ JwtConfiguration, JwtUtil }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GraphQLController @Inject() (
    override protected val controllerComponents: ControllerComponents,
    graphQLSchema: GraphQLSchema,
    graphQLServices: GraphQLServices,
    jwtConfig: JwtConfiguration
)(implicit
    executionContext: ExecutionContext
) extends BaseController
    with Circe
    with Logging { self =>

  private lazy val contextWithoutUser = GraphQLContext.withoutUser(graphQLServices)

  def graphQL: Action[GraphQLRequest] =
    Action.async(circe.tolerantJson[GraphQLRequest]) { request =>
      val graphQLContext = request.headers
        .get(GraphQLController.userTokenHeader)
        .toRight(ServerError.Authentication.Token.Missing)
        .flatMap(JwtUtil.validateJwt(_, jwtConfig.signaturePublicKey))
        .fold(
          error => {
            logger.warn(error.message)
            contextWithoutUser
          },
          jwtContent => GraphQLContext.withUser(graphQLServices, jwtContent.userId)
        )

      QueryParser
        .parse(request.body.query)
        .fold(
          error => Future.successful(BadRequest(GraphQLError(error.getMessage.asJson).asJson)),
          queryAst =>
            executeGraphQLQuery(
              GraphQLQuery(
                queryAst,
                operationName = request.body.operationName,
                variables = request.body.variables.getOrElse(Json.obj())
              ),
              graphQLContext = graphQLContext
            ).map(Ok(_))
        )
    }

  def schema: Action[AnyContent] =
    Action {
      Ok(graphQLSchema.schema.renderPretty)
    }

  private def executeGraphQLQuery(graphQLQuery: GraphQLQuery, graphQLContext: GraphQLContext): Future[Json] =
    Executor.execute[GraphQLContext, Unit, Json](
      schema = graphQLSchema.schema,
      queryAst = graphQLQuery.query,
      userContext = graphQLContext,
      operationName = graphQLQuery.operationName,
      variables = graphQLQuery.variables
    )

}

object GraphQLController {
  val userTokenHeader: String = "User-Token"
}
