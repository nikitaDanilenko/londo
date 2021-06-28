package graphql.types.util

import cats.Functor
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.{ FromInternal, ToInternal }
import io.circe.Decoder
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, InputType, ObjectType, OutputType }

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

  implicit def nonEmptyListInputType[A: InputType]: InputObjectType[NonEmptyList[A]] =
    deriveInputObjectType[NonEmptyList[A]](
      InputObjectTypeName("NonEmptyListInput")
    )

  implicit def nonEmptyListFromInput[A: Decoder]: FromInput[NonEmptyList[A]] = circeDecoderFromInput[NonEmptyList[A]]

  implicit def nonEmptyListOutputType[A: OutputType]: ObjectType[Unit, NonEmptyList[A]] =
    deriveObjectType[Unit, NonEmptyList[A]]()

}
