package controllers

import cats.data.{ EitherT, OptionT }
import db.{ SessionId, UserId }
import errors.ErrorContext
import io.scalaland.chimney.dsl.TransformerOps
import play.api.mvc._
import security.jwt.{ JwtConfiguration, LoggedIn }
import services.session.SessionService
import utils.jwt.JwtUtil
import utils.transformer.implicits._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.chaining._

class JWTAction @Inject() (
    sessionService: SessionService,
    jwtConfiguration: JwtConfiguration,
    override val parser: BodyParsers.Default
)(implicit
    override val executionContext: ExecutionContext
) extends ActionBuilder[LoggedInRequest, AnyContent]
    with ActionRefiner[Request, LoggedInRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, LoggedInRequest[A]]] = {
    OptionT
      .fromOption[Future](request.headers.get(RequestHeaders.userToken))
      .flatMapF { token =>
        val transformer = for {
          loggedIn <- EitherT.fromEither[Future](
            JwtUtil.validateJwt[LoggedIn](token, jwtConfiguration.signaturePublicKey)
          )
          userId    = loggedIn.userId.transformInto[UserId]
          sessionId = loggedIn.sessionId.transformInto[SessionId]
          _ <- EitherT(
            sessionService
              .exists(
                userId = userId,
                sessionId = sessionId
              )
              .map { exists =>
                if (exists)
                  Right(())
                else Left(ErrorContext.Session.NotFound.asServerError)
              }
          )
        } yield LoggedIn(
          userId = userId,
          sessionId = sessionId
        )

        transformer.fold(_ => None, Some(_))
      }
      .value
      .map(LoggedInRequest(request, _).pipe(Right(_)))
  }

}
