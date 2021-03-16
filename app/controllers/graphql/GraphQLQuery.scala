package controllers.graphql

import io.circe.Json
import sangria.ast.Document

case class GraphQLQuery(
    query: Document,
    operationName: Option[String],
    variables: Json
)
