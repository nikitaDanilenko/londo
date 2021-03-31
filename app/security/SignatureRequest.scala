package security

import controllers.SignatureAction
import errors.ServerError
import io.circe.generic.JsonCodec
import play.api.mvc.Request

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
      .get(SignatureAction.authenticationInstantHeader)
      .toRight(ServerError.Authentication.MissingAuthenticationInstant)
      .map { time =>
        SignatureRequest(
          httpVerb = request.method,
          authenticationInstant = Instant.parse(time),
          bodyHash = Hash.messageDigest(request.body.toString)
        )
      }
  }

}
