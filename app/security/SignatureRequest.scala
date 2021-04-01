package security

import cats.effect.IO
import controllers.RequestHeaders
import errors.ServerError
import io.circe.generic.JsonCodec
import io.circe.syntax._
import play.api.mvc.{ Request, Result }

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

  def fromResult(method: String, result: Result): IO[SignatureRequest] =
    IO {
      SignatureRequest(
        httpVerb = method,
        authenticationInstant = Instant.now,
        bodyHash = Hash.messageDigest(result.body.toString)
      )
    }

  def hashOf(signatureRequest: SignatureRequest): String =
    Hash.messageDigest(signatureRequest.asJson.noSpaces)

}
