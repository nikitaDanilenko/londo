package graphql.types.util

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

sealed abstract case class Positive(
    positive: Int
)

object Positive {

  private def apply(positive: Int): Positive = new Positive(positive) {}

  implicit val positiveCodec: Codec[Positive] =
    deriveCodec[Positive].iemap(p => Right(p).filterOrElse(_.positive > 0, "Not a positive natural number"))(identity)

  implicit val toInternal: Transformer[Positive, math.Positive] = positive =>
    math.Positive.nextOf(spire.math.Natural(positive.positive - 1))

  implicit val fromInternal: Transformer[math.Positive, Positive] = positive => Positive(positive.natural.intValue)

  implicit val positiveObjectType: ObjectType[Unit, Positive] = deriveObjectType[Unit, Positive]()

  implicit val positiveInputObjectType: InputObjectType[Positive] = deriveInputObjectType[Positive](
    InputObjectTypeName("PositiveInput")
  )

  implicit lazy val positiveFromInput: FromInput[Positive] = circeDecoderFromInput[Positive]
}
