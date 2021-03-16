package controllers.graphql

import io.circe.Json
import io.circe.generic.JsonCodec

@JsonCodec
case class GraphQLError(error: Json)
