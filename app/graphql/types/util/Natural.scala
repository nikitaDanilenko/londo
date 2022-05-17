package graphql.types.util

import graphql.types.FromAndToInternal
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }

case class Natural(
    nonNegative: BigInt
)

object Natural {

  implicit val naturalEncoder: Encoder[Natural] = deriveEncoder

  implicit val naturalDecoder: Decoder[Natural] = deriveDecoder[Natural].emap { n =>
    Right(n).filterOrElse(_.nonNegative >= 0, "Non a non-negative number")
  }

  implicit val naturalFromInternal: FromAndToInternal[Natural, spire.math.Natural] = FromAndToInternal.create(
    fromInternal = n => Natural(n.toBigInt),
    toInternal = n => spire.math.Natural(n.nonNegative)
  )

  implicit val naturalObjectType: ObjectType[Unit, Natural] = deriveObjectType[Unit, Natural]()

  implicit val naturalInputObjectType: InputObjectType[Natural] = deriveInputObjectType[Natural](
    InputObjectTypeName("NaturalInput")
  )

  implicit lazy val naturalFromInput: FromInput[Natural] = circeDecoderFromInput[Natural]
}
