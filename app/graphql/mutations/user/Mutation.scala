package graphql.mutations.user

import cats.data.EitherT
import cats.effect.unsafe.implicits.global
import cats.syntax.functor._
import errors.{ ErrorContext, ServerError }
import graphql.HasGraphQLServices.syntax._
import graphql.{ HasGraphQLServices, HasLoggedInUser }
import io.circe.Encoder
import io.scalaland.chimney.dsl._
import sangria.macros.derive.GraphQLField
import security.Hash
import security.jwt.{ JwtConfiguration, JwtExpiration, LoggedIn }
import services.loginThrottle.LoginThrottle
import services.user.PasswordParameters
import utils.date.DateUtil
import utils.jwt.JwtUtil
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

trait Mutation extends HasGraphQLServices with HasLoggedInUser {

  private val jwtConfiguration  = JwtConfiguration.default
  private val userConfiguration = UserHandlingConfiguration.default

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

    def handleJWTGeneration(
        passwordCorrect: Boolean,
        userId: db.UserId
    ): EitherT[Future, ServerError, String] =
      if (passwordCorrect)
        for {
          session <- EitherT(graphQLServices.sessionService.create(userId))
        } yield JwtUtil.createJwt(
          content = LoggedIn(
            userId = userId,
            sessionId = session.id
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
      else
        EitherT.leftT[Future, String](ErrorContext.Login.Failure.asServerError)

    val transformer = for {
      user <- EitherT.fromOptionF(
        graphQLServices.userService.getByNickname(nickname),
        ErrorContext.User.NotFound.asServerError
      )
      loginThrottle <- getOrCreateThrottle(user.id)
      passwordCorrect = Hash.verify(
        password,
        passwordParameters = PasswordParameters(
          user.hash,
          user.salt,
          Hash.defaultIterations
        )
      )
      now <- EitherT.liftF[Future, ServerError, LocalDateTime](DateUtil.now.unsafeToFuture())
      _ <- handleThrottle(
        userId = user.id,
        throttle = loginThrottle,
        passwordCorrect = passwordCorrect,
        now = now
      )
      jwt <- handleJWTGeneration(
        passwordCorrect = passwordCorrect,
        userId = user.id
      )
    } yield jwt

    transformer.value.handleServerError
  }

  @GraphQLField
  def logout(logoutMode: LogoutMode): Future[Boolean] = {
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
  def requestRegistration(
      userIdentifier: UserIdentifier
  ): Future[Unit] = {
    val transformer = for {
      _ <- EitherT.fromOptionF(
        graphQLServices.userService
          .getByNickname(userIdentifier.nickname)
          .map(r => if (r.isDefined) None else Some(())),
        ErrorContext.User.Exists.asServerError
      )
      registrationJwt = createJwt(userIdentifier)
      _ <- EitherT(
        graphQLServices.emailService
          .sendEmail(
            emailParameters = UserHandlingConfiguration.registrationEmail(
              userConfiguration = userConfiguration,
              userIdentifier = userIdentifier,
              jwt = registrationJwt
            )
          )
      )
    } yield ()
    transformer.value.handleServerError
  }

  @GraphQLField
  def confirmRegistration(
      creationToken: String,
      creationComplement: CreationComplement
  ): Future[User] = {
    val transformer = for {
      registrationRequest <- EitherT.fromEither[Future](
        JwtUtil.validateJwt[UserIdentifier](creationToken, jwtConfiguration.signaturePublicKey)
      )
      userCreation = services.user.Creation(
        nickname = registrationRequest.nickname,
        password = creationComplement.password,
        displayName = creationComplement.displayName,
        email = registrationRequest.email
      )
      user <- EitherT.liftF[Future, ServerError, services.user.User](
        services.user.Creation.create(userCreation).unsafeToFuture()
      )
      response <- EitherT(graphQLServices.userService.add(user))
    } yield response.transformInto[User]

    transformer.value.handleServerError
  }

  @GraphQLField
  def requestRecovery(
      userId: UserId
  ): Future[Unit] = {
    val transformer = for {
      user <- EitherT.fromOptionF(
        graphQLServices.userService.get(userId.transformInto[db.UserId]),
        ErrorContext.User.NotFound.asServerError
      )
      recoveryJwt = createJwt(
        UserOperation(
          userId = user.id,
          operation = UserOperation.Operation.Recovery
        )
      )
      _ <- EitherT(
        graphQLServices.emailService
          .sendEmail(
            emailParameters = UserHandlingConfiguration.recoveryEmail(
              userConfiguration,
              userIdentifier = UserIdentifier.of(user),
              jwt = recoveryJwt
            )
          )
          .map(Right(_))
          .recover { case _ =>
            Left(ErrorContext.Mail.SendingFailed.asServerError)
          }
      )
    } yield ()

    transformer.value.handleServerError
  }

  @GraphQLField
  def confirmRecovery(
      recoveryToken: String,
      password: String
  ): Future[Boolean] = {
    val transformer = for {
      userRecovery <- EitherT.fromEither[Future](
        JwtUtil
          .validateJwt[UserOperation[UserOperation.Operation.Recovery]](
            recoveryToken,
            jwtConfiguration.signaturePublicKey
          )
      )
      successful <-
        EitherT(
          graphQLServices.userService
            .updatePassword(userRecovery.userId.transformInto[db.UserId], password)
        )
    } yield successful

    transformer.value.handleServerError
  }

  private def createJwt[C: Encoder](
      content: C,
      expiration: JwtExpiration = JwtExpiration.Expiring(
        start = System.currentTimeMillis() / 1000,
        duration = userConfiguration.restrictedDurationInSeconds
      )
  ): String =
    JwtUtil.createJwt(
      content = content,
      privateKey = jwtConfiguration.signaturePrivateKey,
      expiration = expiration
    )

}
