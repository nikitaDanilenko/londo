package graphql.types.util

import cats.Functor
import io.circe.generic.JsonCodec
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._

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

  implicit def fromInternal[Internal, A](implicit
      transformer: Transformer[Internal, A]
  ): Transformer[cats.data.NonEmptyList[Internal], NonEmptyList[A]] = { nonEmptyList =>
    val mapped = nonEmptyList.map(_.transformInto[A])
    NonEmptyList(
      head = mapped.head,
      tail = mapped.tail
    )
  }

  implicit def toInternal[A, Internal](implicit
      transformer: Transformer[A, Internal]
  ): Transformer[NonEmptyList[A], cats.data.NonEmptyList[Internal]] =
    nonEmptyList =>
      cats.data.NonEmptyList(
        head = nonEmptyList.head.transformInto[Internal],
        tail = nonEmptyList.tail.toList.map(_.transformInto[Internal])
      )

}
