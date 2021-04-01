package security

import controllers.RequestHeaders
import errors.ServerError
import io.circe.generic.JsonCodec
import play.api.mvc.Request
import io.circe.syntax._

import java.time.Instant

@JsonCodec
case class SignatureRequest(
    httpVerb: String,
    authenticationInstant: Instant,
    bodyHash: String
)

object SignatureRequest {

  def fromRequest[A](request: Request[A]): ServerError.Or[SignatureRequest] = {
    request.headers
      .get(RequestHeaders.authenticationInstantHeader)
      .toRight(ServerError.Authentication.Signature.MissingInstant)
      .map { time =>
        SignatureRequest(
          httpVerb = request.method,
          authenticationInstant = Instant.parse(time),
          bodyHash = Hash.messageDigest(request.body.toString)
        )
      }
  }

  def hashOf(signatureRequest: SignatureRequest): String =
    Hash.messageDigest(signatureRequest.asJson.noSpaces)

}
