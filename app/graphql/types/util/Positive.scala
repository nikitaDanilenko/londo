package graphql.types.util

import graphql.types.FromAndToInternal
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

sealed abstract case class Positive(
    positive: BigInt
)

object Positive {

  private def apply(positive: BigInt): Positive = new Positive(positive) {}

  implicit val positiveCodec: Codec[Positive] =
    deriveCodec[Positive].iemap(p => Right(p).filterOrElse(_.positive > 0, "Not a positive natural number"))(identity)

  implicit val positiveFromInternal: FromAndToInternal[Positive, math.Positive] = FromAndToInternal.create(
    fromInternal = p => Positive(p.natural.toBigInt),
    toInternal = p => math.Positive.nextOf(spire.math.Natural(p.positive - 1))
  )

  implicit val positiveObjectType: ObjectType[Unit, Positive] = deriveObjectType[Unit, Positive]()

  implicit val positiveInputObjectType: InputObjectType[Positive] = deriveInputObjectType[Positive](
    InputObjectTypeName("PositiveInput")
  )

  implicit lazy val positiveFromInput: FromInput[Positive] = circeDecoderFromInput[Positive]
}
