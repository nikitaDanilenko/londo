package graphql.mutations

import cats.data.{ EitherT, OptionT }
import errors.{ ErrorContext, ServerError }
import cats.syntax.functor._
import graphql.HasGraphQLServices.syntax._
import graphql.types.user.{ LogoutMode, SessionId, User, UserCreation, UserId, UserUpdate }
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import sangria.macros.derive.GraphQLField
import security.Hash
import security.jwt.{ JwtConfiguration, JwtExpiration, LoggedIn }
import services.user.PasswordParameters
import utils.jwt.JwtUtil
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import cats.effect.unsafe.implicits.global
import services.loginThrottle.LoginThrottle

import java.time.temporal.ChronoUnit
import java.time.{ LocalDate, LocalDateTime }
import scala.concurrent.Future
import util.chaining._

trait UserMutation extends HasGraphQLServices with HasLoggedInUser {

  private val jwtConfiguration = JwtConfiguration.default

  private val maxLoginAttempts       = 5
  private val loginThrottleInMinutes = 1

  @GraphQLField
  def login(
      nickname: String,
      password: String,
      isValidityUnrestricted: Boolean
  ): Future[String] = {
    // TODO: Reconsider style!
    def getOrCreateThrottle(userId: db.UserId): EitherT[Future, ServerError, LoginThrottle] =
      EitherT {
        graphQLServices.loginThrottleService
          .get(userId)
          .flatMap {
            _.fold(graphQLServices.loginThrottleService.create(userId))(x => Future.successful(Right(x)))
          }
      }

    def handleThrottle(
        userId: db.UserId,
        throttle: LoginThrottle,
        passwordCorrect: Boolean,
        now: LocalDateTime
    ): EitherT[Future, ServerError, Unit] = {
      lazy val unblockedAt = throttle.lastAttemptAt.plusMinutes(loginThrottleInMinutes)
      if (throttle.failedAttempts > maxLoginAttempts && now.isBefore(unblockedAt)) {
        // Block directly, without any updates, or password check
        val seconds = ChronoUnit.SECONDS.between(now, unblockedAt)
        EitherT.leftT[Future, Unit](
          ErrorContext.Login.Limit(s"Please wait another $seconds seconds").asServerError
        )
      } else {
        // Any other case: No throttling, but the login attempt is updated.
        EitherT(
          graphQLServices.loginThrottleService.update(
            userId,
            services.loginThrottle.Update(
              failedAttempts = if (passwordCorrect) 0 else 1 + throttle.failedAttempts,
              lastAttemptAt = now
            )
          )
        ).void
      }
    }

    val transformer = for {
      user <- EitherT.fromOptionF(
        graphQLServices.userService.getByNickname(nickname),
        ErrorContext.User.NotFound.asServerError
      )
      userId = user.id
      loginThrottle <- getOrCreateThrottle(userId)
      valid = Hash.verify(
        password,
        passwordParameters = PasswordParameters(
          user.hash,
          user.salt,
          Hash.defaultIterations
        )
      )
      now <- EitherT.liftF[Future, ServerError, LocalDateTime](DateUtil.now.unsafeToFuture())
      _   <- handleThrottle(userId, loginThrottle, valid, now)
      jwt <-
        if (valid)
          for {
            session <- EitherT(graphQLServices.sessionService.create(user.id))
          } yield JwtUtil.createJwt(
            content = LoggedIn(
              userId = user.id.transformInto[UserId],
              sessionId = session.id.transformInto[SessionId]
            ),
            privateKey = jwtConfiguration.signaturePrivateKey,
            expiration =
              if (isValidityUnrestricted) JwtExpiration.Never
              else
                JwtExpiration.Expiring(
                  start = System.currentTimeMillis() / 1000,
                  duration = jwtConfiguration.restrictedDurationInSeconds
                )
          )
        else {
          EitherT.leftT[Future, String](ErrorContext.Login.Failure.asServerError)
        }
    } yield jwt

    transformer.value.handleServerError
  }

  @GraphQLField
  def logout(logoutMode: graphql.types.user.LogoutMode): Future[Boolean] = {
    withUser { loggedIn =>
      val userId = loggedIn.userId.transformInto[db.UserId]
      val action = logoutMode match {
        case LogoutMode.ThisSession =>
          graphQLServices.sessionService
            .delete(userId, loggedIn.sessionId.transformInto[db.SessionId])
        case LogoutMode.AllSessions =>
          graphQLServices.sessionService
            .deleteAll(userId)
      }
      action.handleServerError
    }
  }

  @GraphQLField
  def update(userUpdate: UserUpdate): Future[User] =
    withUser { loggedIn =>
      EitherT(
        graphQLServices.userService
          .update(
            loggedIn.userId.transformInto[db.UserId],
            userUpdate.transformInto[services.user.Update]
          )
      )
        .map(_.transformInto[User])
        .value
        .handleServerError
    }

  @GraphQLField
  def updatePassword(password: String): Future[Boolean] =
    withUser { loggedIn =>
      EitherT(
        graphQLServices.userService
          .updatePassword(loggedIn.userId.transformInto[db.UserId], password)
      ).value.handleServerError
    }

  @GraphQLField
  def requestCreate(email: String): Future[Unit] = ???
//    graphQLServices.userService
//      .requestCreate(email)
//      .unsafeToFuture()
//      .handleServerError

  @GraphQLField
  def createUser(userCreation: UserCreation): Future[User] = ???
//    graphQLServices.userService
//      .create(userCreation.toInternal)
//      .map(_.fromInternal[User])
//      .unsafeToFuture()
//      .handleServerError

}
