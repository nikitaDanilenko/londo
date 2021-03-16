package controllers.graphql

import io.circe.generic.semiauto.deriveDecoder
import io.circe.{ Decoder, Json }

case class GraphQLRequest(
    query: String,
    operationName: Option[String],
    variables: Option[Json]
)

object GraphQLRequest {

  /* Based upon https://gist.github.com/trbngr/571e7fc2b98c4c5559ab9af2e4dc802d
   *  A workaround is necessary, because variables may have different shapes. */
  implicit val decoder: Decoder[GraphQLRequest] = deriveDecoder[GraphQLRequest].emap { request =>
    request.variables match {
      case Some(value) if value.isString =>
        val variables = value.withString(str => io.circe.parser.parse(str).getOrElse(value))
        Right(request.copy(variables = Some(variables)))
      case _ => Right(request)
    }
  }

}
