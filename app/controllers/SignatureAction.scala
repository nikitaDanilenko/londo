package controllers

import cats.data.EitherT
import cats.effect.{ ContextShift, IO }
import db.ContextShiftProvider
import db.generated.daos.SessionKeyDAO
import db.keys.UserId
import db.models.SessionKey
import errors.{ ErrorContext, ServerError }
import io.circe.syntax._
import play.api.libs.circe.Circe
import play.api.mvc.Results.BadRequest
import play.api.mvc.{ BodyParsers, _ }
import security.{ SignatureConfiguration, SignatureRequest }
import security.jwt.JwtConfiguration
import utils.jwt.JwtUtil
import utils.signature.{ DiffieHellman, SignatureHandler }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class SignatureAction @Inject() (
    override val parse: PlayBodyParsers,
    sessionKeyDAO: SessionKeyDAO,
    jwtConfiguration: JwtConfiguration,
    signatureConfiguration: SignatureConfiguration
)(implicit
    override val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent]
    with Circe {

  private implicit val contextShift: ContextShift[IO] = ContextShiftProvider.fromExecutionContext

  override def invokeBlock[A](
      request: Request[A],
      block: Request[A] => Future[Result]
  ): Future[Result] = {
    request.headers.get(RequestHeaders.userTokenHeader) match {
      case Some(token) =>
        val transformer = for {
          jwtContent <- EitherT.fromEither[Future](JwtUtil.validateJwt(token, jwtConfiguration.signaturePublicKey))
          sessionKey <- EitherT.fromOptionF[Future, ServerError, SessionKey](
            sessionKeyDAO.find[IO](UserId(jwtContent.userId.uuid)).unsafeToFuture(),
            ErrorContext.Authentication.Token.MissingSessionKey.asServerError
          )
          signatureRequest <- EitherT.fromEither[Future](SignatureRequest.fromRequest(request))
          signature <- EitherT.fromOption[Future](
            request.headers.get(RequestHeaders.authenticationHeader),
            ErrorContext.Authentication.Signature.Missing.asServerError
          )
          result <- {
            val sharedSecret = DiffieHellman.sharedSecret(
              modulus = signatureConfiguration.modulus,
              publicNumber = BigInt(sessionKey.publicKey),
              privateExponent = signatureConfiguration.backendExponent
            )
            if (
              SignatureHandler.validate(
                signature = signature,
                message = SignatureRequest.hashOf(signatureRequest),
                secret = sharedSecret
              )
            ) {
              val resultWithExtraHeader =
                block(request).map(_.withHeaders(RequestHeaders.authenticationUserKey -> sessionKey.publicKey))
              EitherT.liftF[Future, ServerError, Result](resultWithExtraHeader)
            } else
              EitherT.leftT[Future, Result].apply(ErrorContext.Authentication.Signature.Invalid.asServerError)
          }
        } yield result
        transformer.valueOr(error => BadRequest(error.asJson))
      case None => block(request)
    }
  }

  override val parser: BodyParser[AnyContent] = new BodyParsers.Default(parse)
}
