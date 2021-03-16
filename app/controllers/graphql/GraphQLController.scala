package controllers.graphql

import graphql._
import io.circe.Json
import io.circe.syntax._
import play.api.libs.circe.Circe
import play.api.mvc.{ Action, AnyContent, BaseController, ControllerComponents }
import sangria.execution.Executor
import sangria.marshalling.circe._
import sangria.parser.QueryParser

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class GraphQLController @Inject() (
    override protected val controllerComponents: ControllerComponents,
    graphQLSchema: GraphQLSchema,
    graphQLServices: GraphQLServices
)(implicit
    executionContext: ExecutionContext
) extends BaseController
    with Circe { self =>

  def graphQL: Action[GraphQLRequest] =
    Action.async(circe.tolerantJson[GraphQLRequest]) { request =>
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
              )
            ).map(Ok(_))
        )
    }

  def schema: Action[AnyContent] =
    Action {
      Ok(graphQLSchema.schema.renderPretty)
    }

  private val graphQLContext = GraphQLContext(graphQLServices)

  private def executeGraphQLQuery(graphQLQuery: GraphQLQuery): Future[Json] =
    Executor.execute[GraphQLContext, Unit, Json](
      schema = graphQLSchema.schema,
      queryAst = graphQLQuery.query,
      userContext = graphQLContext,
      operationName = graphQLQuery.operationName,
      variables = graphQLQuery.variables
    )

}
