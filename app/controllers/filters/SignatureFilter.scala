package controllers.filters

import controllers.RequestHeaders
import play.api.mvc.{ EssentialAction, EssentialFilter }
import security.{ SignatureConfiguration, SignatureRequest }
import utils.signature.SignatureHandler

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class SignatureFilter @Inject() (signatureConfiguration: SignatureConfiguration)(implicit
    executionContext: ExecutionContext
) extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = { requestHeader =>
    next(requestHeader).mapFuture { result =>
      SignatureRequest
        .fromResult(requestHeader.method, result)
        .map { signatureRequest =>
          val signature = SignatureHandler.sign(
            SignatureRequest.hashOf(signatureRequest),
            signatureConfiguration.privateKey
          )
          result
            .withHeaders(
              RequestHeaders.authenticationHeader -> signature,
              RequestHeaders.authenticationInstantHeader -> signatureRequest.authenticationInstant.toString
            )
            .discardingHeader(RequestHeaders.authenticationUserId)
        }
        .unsafeToFuture()
    }
  }

}
