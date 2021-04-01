package controllers

import play.api.mvc.{ EssentialAction, EssentialFilter }
import security.SignatureRequest
import security.jwt.JwtConfiguration
import utils.signature.SignatureHandler

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SignatureFilter @Inject() (jwtConfiguration: JwtConfiguration)(implicit executionContext: ExecutionContext)
    extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = { requestHeader =>
    next(requestHeader).mapFuture { result =>
      SignatureRequest
        .fromResult(requestHeader.method, result)
        .map { signatureRequest =>
          val signature = SignatureHandler.sign(
            SignatureRequest.hashOf(signatureRequest),
            jwtConfiguration.signaturePrivateKey
          )
          result.withHeaders(
            RequestHeaders.authenticationHeader -> signature,
            RequestHeaders.authenticationInstantHeader -> signatureRequest.authenticationInstant.toString
          )
        }
        .unsafeToFuture()

    }
  }

}
