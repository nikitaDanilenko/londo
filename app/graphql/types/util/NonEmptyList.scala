package graphql.types.util

import cats.Functor
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.{ FromInternal, ToInternal }
import io.circe.generic.JsonCodec

@JsonCodec
case class NonEmptyList[A](
    head: A,
    tail: Seq[A]
)

object NonEmptyList {

  implicit val nonEmptyListFunctor: Functor[NonEmptyList] = new Functor[NonEmptyList] {

    override def map[A, B](fa: NonEmptyList[A])(
        f: A => B
    ): NonEmptyList[B] =
      NonEmptyList(
        f(fa.head),
        fa.tail.map(f)
      )

  }

  implicit def nonEmptyListFromInternal[G, InternalA](implicit
      fromInternal: FromInternal[G, InternalA]
  ): FromInternal[NonEmptyList[G], cats.data.NonEmptyList[InternalA]] = { nonEmptyList =>
    val mapped = nonEmptyList.map(_.fromInternal)
    NonEmptyList(
      head = mapped.head,
      tail = mapped.tail
    )
  }

  implicit def nonEmptyListToInternal[A, InternalA](implicit
      toInternal: ToInternal[A, InternalA]
  ): ToInternal[NonEmptyList[A], cats.data.NonEmptyList[InternalA]] =
    nonEmptyList =>
      cats.data.NonEmptyList(
        head = nonEmptyList.head.toInternal,
        tail = nonEmptyList.tail.toList.map(_.toInternal)
      )

}
