package graphql.types.util

import graphql.types.FromAndToInternal
import io.circe.generic.semiauto.{ deriveCodec, deriveDecoder }
import io.circe.{ Codec, Decoder }
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import util.chaining._

sealed abstract case class Natural(
    nonNegative: Int
)

object Natural {

  private def apply(nonNegative: Int): Natural = new Natural(nonNegative) {}

  implicit val naturalCodec: Codec[Natural] =
    deriveCodec[Natural].iemap(
      Right(_).filterOrElse(_.nonNegative >= 0, "Non a non-negative number")
    )(identity)

  implicit val fromInternal: Transformer[spire.math.Natural, Natural] =
    _.intValue.pipe(Natural.apply)

  implicit val toInternal: Transformer[Natural, spire.math.Natural] =
    _.nonNegative.pipe(spire.math.Natural.apply(_))

  implicit val naturalObjectType: ObjectType[Unit, Natural] = deriveObjectType[Unit, Natural]()

  implicit val naturalInputObjectType: InputObjectType[Natural] = deriveInputObjectType[Natural](
    InputObjectTypeName("NaturalInput")
  )

  implicit lazy val naturalFromInput: FromInput[Natural] = circeDecoderFromInput[Natural]
}
