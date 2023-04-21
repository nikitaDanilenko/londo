package controllers

import cats.data.EitherT
import db.{ SessionId, UserId }
import errors.{ ErrorContext, ServerError }
import io.circe.syntax._
import io.scalaland.chimney.dsl.TransformerOps
import play.api.libs.circe.Circe
import play.api.mvc.Results.BadRequest
import play.api.mvc._
import security.jwt.JwtConfiguration
import services.session.SessionService
import utils.jwt.JwtUtil
import utils.transformer.implicits._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class JWTAction @Inject() (
    override val parse: PlayBodyParsers,
    sessionService: SessionService
)(implicit
    override val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent]
    with Circe {

  private val jwtConfiguration = JwtConfiguration.default

  override def invokeBlock[A](
      request: Request[A],
      block: Request[A] => Future[Result]
  ): Future[Result] =
    request.headers.get(RequestHeaders.userTokenHeader) match {
      case Some(token) =>
        val transformer = for {
          jwtContent <- EitherT.fromEither[Future](JwtUtil.validateJwt(token, jwtConfiguration.signaturePublicKey))
          _ <- EitherT(
            sessionService
              .exists(
                userId = jwtContent.userId.uuid.transformInto[UserId],
                sessionId = jwtContent.sessionId.uuid.transformInto[SessionId]
              )
              .map(Either.cond(_, (), ErrorContext.Authentication.Token.MissingSessionKey.asServerError))
          )
          result <- EitherT.liftF[Future, ServerError, Result](block(request))
        } yield result
        transformer.valueOr(error => BadRequest(error.asJson))
      case None => block(request)
    }

  override val parser: BodyParser[AnyContent] = new BodyParsers.Default(parse)
}
