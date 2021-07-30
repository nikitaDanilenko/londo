package graphql

import cats.ApplicativeThrow
import cats.data.{ EitherT, NonEmptySet }
import cats.effect.{ ContextShift, IO, MonadThrow }
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import errors.{ ErrorContext, ServerError, ServerException }
import graphql.HasGraphQLServices.syntax._
import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.dashboard.DashboardId
import graphql.types.project.ProjectId
import graphql.types.user.UserId
import services.access.Accessors
import services.dashboard.DashboardService
import services.project.ProjectService

import scala.concurrent.{ ExecutionContext, Future }

trait HasLoggedInUser {
  protected def loggedInUserId: Option[UserId]

  final protected def validateAccess[F[_]: ApplicativeThrow](accessedUserId: UserId): F[services.user.UserId] =
    allowedAccess(Accessors.NobodyExcept(NonEmptySet.of(accessedUserId.toInternal)))

  final protected def allowedAccess[F[_]: ApplicativeThrow](accessors: Accessors): F[services.user.UserId] =
    ApplicativeThrow[F].fromEither(
      loggedInUserId
        .filter(userId => Accessors.hasAccess(userId.toInternal, accessors))
        .toRight(ServerException(ErrorContext.Authentication.Token.Restricted.asServerError))
        .map(_.toInternal)
    )

  final protected def withUser[F[_]: MonadThrow, A](create: UserId => F[A]): F[A] =
    ApplicativeThrow[F]
      .fromOption(
        loggedInUserId,
        ServerException(ErrorContext.Authentication.Token.Restricted.asServerError)
      )
      .flatMap(create)

  final protected def allowedAccessVia[F[_]: MonadThrow, A, B](
      fa: F[A]
  )(accessorsOf: A => Accessors, conversion: A => B): F[B] =
    fa.flatMap(a => allowedAccess(accessorsOf(a)).map(_ => conversion(a)))

  final protected def allowedAccessViaError[F[_]: MonadThrow, A, B](
      fa: F[ServerError.Or[A]]
  )(accessorsOf: A => Accessors, conversion: A => B): F[ServerError.Or[B]] =
    fa.flatMap(_.traverse(a => allowedAccess(accessorsOf(a)).map(_ => conversion(a))))

  def validateProjectAccess[A](
      projectService: ProjectService,
      projectId: ProjectId,
      accessorsOf: services.project.Project => Accessors
  )(
      f: (services.user.UserId, services.project.Project) => IO[ServerError.Or[A]]
  )(implicit contextShift: ContextShift[IO], executionContext: ExecutionContext): Future[A] =
    validateAccessGeneric(
      fetchFunction = projectService.fetch[IO],
      graphQLId = projectId,
      accessorsOf = accessorsOf
    )(f)

  def validateDashboardAccess[A](
      dashboardService: DashboardService,
      dashboardId: DashboardId,
      accessorsOf: services.dashboard.Dashboard => Accessors
  )(
      f: (services.user.UserId, services.dashboard.Dashboard) => IO[ServerError.Or[A]]
  )(implicit contextShift: ContextShift[IO], executionContext: ExecutionContext): Future[A] =
    validateAccessGeneric(
      fetchFunction = dashboardService.fetch[IO],
      graphQLId = dashboardId,
      accessorsOf = accessorsOf
    )(f)

  def validateAccessGeneric[GraphQLId, InternalId, T, Result](
      fetchFunction: InternalId => IO[ServerError.Or[T]],
      graphQLId: GraphQLId,
      accessorsOf: T => Accessors
  )(f: (services.user.UserId, T) => IO[ServerError.Or[Result]])(implicit
      toInternal: ToInternal[GraphQLId, InternalId],
      contextShift: ContextShift[IO],
      executionContext: ExecutionContext
  ): Future[Result] = {
    EitherT(
      fetchFunction(graphQLId.toInternal)
    ).flatMap(t =>
      EitherT
        .liftF[IO, ServerError, services.user.UserId](
          allowedAccess[IO](accessorsOf(t))
        )
        .flatMap(userId => EitherT(f(userId, t)))
    ).value
      .unsafeToFuture()
      .handleServerError
  }

}
