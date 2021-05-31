package graphql.types.util

import cats.Functor
import io.circe.Decoder
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.schema.{ InputObjectType, InputType, ObjectType, OutputType }
import sangria.marshalling.circe.circeDecoderFromInput

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

  implicit def nonEmptyListInputType[A: InputType]: InputObjectType[NonEmptyList[A]] =
    deriveInputObjectType[NonEmptyList[A]](
      InputObjectTypeName("NonEmptyListInput")
    )

  implicit def nonEmptyListFromInput[A: Decoder]: FromInput[NonEmptyList[A]] = circeDecoderFromInput[NonEmptyList[A]]

  implicit def nonEmptyListOutputType[A: OutputType]: ObjectType[Unit, NonEmptyList[A]] =
    deriveObjectType[Unit, NonEmptyList[A]]()

  def fromInternal[A](nonEmptyList: cats.data.NonEmptyList[A]): NonEmptyList[A] =
    NonEmptyList(
      head = nonEmptyList.head,
      tail = nonEmptyList.tail
    )

  def toInternal[A](nonEmptyList: NonEmptyList[A]): cats.data.NonEmptyList[A] =
    cats.data.NonEmptyList(
      head = nonEmptyList.head,
      tail = nonEmptyList.tail.toList
    )

}
