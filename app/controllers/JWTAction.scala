package controllers

import cats.data.EitherT
import cats.effect.{ ContextShift, IO }
import db.ContextShiftProvider
import db.generated.daos.SessionKeyDAO
import db.keys.{ SessionId, SessionKeyId, UserId }
import db.models.SessionKey
import errors.{ ErrorContext, ServerError }
import io.circe.syntax._
import play.api.libs.circe.Circe
import play.api.mvc.Results.BadRequest
import play.api.mvc.{ BodyParsers, _ }
import security.jwt.JwtConfiguration
import utils.jwt.JwtUtil

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class JWTAction @Inject() (
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
            sessionKeyDAO
              .find[IO](
                SessionKeyId(
                  userId = UserId(jwtContent.userId.uuid),
                  sessionId = SessionId(jwtContent.sessionId.uuid)
                )
              )
              .unsafeToFuture(),
            ErrorContext.Authentication.Token.MissingSessionKey.asServerError
          )
          result <- {
            val resultWithExtraHeader =
              block(request)
                .map(_.withHeaders(RequestHeaders.authenticationSessionId -> sessionKey.sessionId.toString))
            EitherT.liftF[Future, ServerError, Result](resultWithExtraHeader)
          }
        } yield result
        transformer.valueOr(error => BadRequest(error.asJson))
      case None => block(request)
    }
  }

  override val parser: BodyParser[AnyContent] = new BodyParsers.Default(parse)
}
