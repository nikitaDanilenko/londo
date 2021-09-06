package controllers.filters

import controllers.RequestHeaders
import play.api.mvc.{ EssentialAction, EssentialFilter }
import security.{ SignatureConfiguration, SignatureRequest }
import utils.signature.{ DiffieHellman, SignatureHandler }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class SignatureFilter @Inject() (signatureConfiguration: SignatureConfiguration)(implicit
    executionContext: ExecutionContext
) extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = { requestHeader =>
    next(requestHeader).mapFuture { result =>
      requestHeader.headers.get(RequestHeaders.authenticationUserKey).fold(Future.successful(result)) { userPublicKey =>
        SignatureRequest
          .fromResult(requestHeader.method, result)
          .map { signatureRequest =>
            val sharedSecret = DiffieHellman
              .sharedSecret(
                modulus = signatureConfiguration.modulus,
                publicNumber = BigInt(userPublicKey),
                privateExponent = signatureConfiguration.backendExponent
              )
            val signature = SignatureHandler.sign(
              SignatureRequest.hashOf(signatureRequest),
              sharedSecret
            )
            result
              .withHeaders(
                RequestHeaders.authenticationHeader -> signature,
                RequestHeaders.authenticationInstantHeader -> signatureRequest.authenticationInstant.toString
              )
              .discardingHeader(RequestHeaders.authenticationUserKey)
          }
          .unsafeToFuture()
      }
    }
  }

}
