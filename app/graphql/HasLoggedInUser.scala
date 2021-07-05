package graphql

import cats.ApplicativeThrow
import cats.data.NonEmptySet
import cats.effect.MonadThrow
import errors.{ ServerError, ServerException }
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import services.project.Accessors
import cats.syntax.flatMap._
import cats.syntax.functor._

trait HasLoggedInUser {
  protected def loggedInUserId: Option[UserId]

  final protected def validateAccess[F[_]: ApplicativeThrow](accessedUserId: UserId): F[services.user.UserId] =
    allowedAccess(Accessors.NobodyExcept(NonEmptySet.of(accessedUserId.toInternal)))

  final protected def allowedAccess[F[_]: ApplicativeThrow](accessors: Accessors): F[services.user.UserId] =
    ApplicativeThrow[F].fromEither(
      loggedInUserId
        .filter(userId => Accessors.hasAccess(userId.toInternal, accessors))
        .toRight(ServerException(ServerError.Authentication.Token.Restricted))
        .map(_.toInternal)
    )

  final protected def withUser[F[_]: MonadThrow, A](create: UserId => F[A]): F[A] =
    ApplicativeThrow[F]
      .fromOption(
        loggedInUserId,
        ServerException(ServerError.Authentication.Token.Restricted)
      )
      .flatMap(create)

  final protected def allowedAccessVia[F[_]: MonadThrow, A, B](
      fa: F[A]
  )(accessorsOf: A => Accessors, conversion: A => B): F[B] =
    fa.flatMap(a => allowedAccess(accessorsOf(a)).map(_ => conversion(a)))

  final protected def allowedAccessViaError[F[_]: MonadThrow, A, B](
      fa: F[ServerError.Valid[A]]
  )(accessorsOf: A => Accessors, conversion: A => B): F[ServerError.Valid[B]] =
    fa.flatMap(_.traverse(a => allowedAccess(accessorsOf(a)).map(_ => conversion(a))))

}
