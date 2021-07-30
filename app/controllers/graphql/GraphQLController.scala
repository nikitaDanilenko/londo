package controllers.graphql

import controllers.{ RequestHeaders, SignatureAction }
import errors.ErrorContext
import graphql._
import io.circe.Json
import io.circe.syntax._
import play.api.Logging
import play.api.libs.circe.Circe
import play.api.mvc._
import sangria.execution.{ ErrorWithResolver, Executor, QueryAnalysisError }
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import security.jwt.JwtConfiguration
import utils.jwt.JwtUtil

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GraphQLController @Inject() (
    override protected val controllerComponents: ControllerComponents,
    graphQLSchema: GraphQLSchema,
    graphQLServices: GraphQLServices,
    jwtConfiguration: JwtConfiguration,
    signatureAction: SignatureAction
)(implicit
    executionContext: ExecutionContext
) extends BaseController
    with Circe
    with Logging { self =>

  private lazy val contextWithoutUser = GraphQLContext.withoutUser(graphQLServices)

  def graphQL: Action[GraphQLRequest] =
    signatureAction.async(circe.tolerantJson[GraphQLRequest]) { request =>
      val graphQLContext = request.headers
        .get(RequestHeaders.userTokenHeader)
        .toRight(ErrorContext.Authentication.Token.Missing.asServerError)
        .flatMap(JwtUtil.validateJwt(_, jwtConfiguration.signaturePublicKey))
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
              graphQLQuery = GraphQLQuery(
                queryAst,
                operationName = request.body.operationName,
                variables = request.body.variables.getOrElse(Json.obj())
              ),
              graphQLContext = graphQLContext
            )
        )
    }

  def schema: Action[AnyContent] =
    Action {
      Ok(graphQLSchema.schema.renderPretty)
    }

  private def executeGraphQLQuery(graphQLQuery: GraphQLQuery, graphQLContext: GraphQLContext): Future[Result] =
    Executor
      .execute[GraphQLContext, Unit, Json](
        schema = graphQLSchema.schema,
        queryAst = graphQLQuery.query,
        userContext = graphQLContext,
        operationName = graphQLQuery.operationName,
        variables = graphQLQuery.variables,
        exceptionHandler = GraphQLExceptionHandler.exceptionHandler
      )
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError => BadRequest(error.resolveError)
        case error: ErrorWithResolver  => InternalServerError(error.resolveError)
      }

}
