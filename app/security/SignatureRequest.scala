package security

import cats.effect.IO
import io.circe.generic.JsonCodec
import io.circe.syntax._
import play.api.mvc.Result

import java.time.Instant

@JsonCodec
case class SignatureRequest(
    httpVerb: String,
    authenticationInstant: Instant,
    bodyHash: String
)

object SignatureRequest {

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
