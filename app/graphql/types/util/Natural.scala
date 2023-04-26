package graphql.types.util

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }

import scala.util.chaining._

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

  implicit val objectType: ObjectType[Unit, Natural] = deriveObjectType[Unit, Natural]()

  implicit val inputObjectType: InputObjectType[Natural] = deriveInputObjectType[Natural](
    InputObjectTypeName("NaturalInput")
  )

}
