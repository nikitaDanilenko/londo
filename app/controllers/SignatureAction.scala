package controllers

import cats.data.EitherT
import cats.effect.{ ContextShift, IO }
import db.ContextShiftProvider
import db.generated.daos.SessionKeyDAO
import db.models.SessionKey
import errors.ServerError
import io.circe.syntax._
import play.api.libs.circe.Circe
import play.api.mvc.Results.BadRequest
import play.api.mvc.{ BodyParsers, _ }
import security.SignatureRequest
import security.jwt.JwtConfiguration
import utils.jwt.JwtUtil
import utils.signature.SignatureHandler

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class SignatureAction @Inject() (
    override val parse: PlayBodyParsers,
    sessionKeyDAO: SessionKeyDAO,
    jwtConfiguration: JwtConfiguration
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
            sessionKeyDAO.find[IO](jwtContent.userId).unsafeToFuture(),
            ServerError.Authentication.Token.MissingSessionKey: ServerError
          )
          signatureRequest <- EitherT.fromEither[Future](SignatureRequest.fromRequest(request))
          signature <- EitherT.fromOption[Future](
            request.headers.get(RequestHeaders.authenticationHeader),
            ServerError.Authentication.Signature.Missing
          )
          result <- {
            if (SignatureHandler.validate(signature, SignatureRequest.hashOf(signatureRequest), sessionKey.publicKey))
              EitherT.liftF[Future, ServerError, Result](block(request))
            else
              EitherT.leftT[Future, Result].apply(ServerError.Authentication.Signature.Invalid: ServerError)
          }
        } yield result
        transformer.valueOr(error => BadRequest(error.asJson))
      case None => block(request)
    }
  }

  override val parser: BodyParser[AnyContent] = new BodyParsers.Default(parse)
}
