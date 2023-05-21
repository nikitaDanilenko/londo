package graphql.types.util

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.scalaland.chimney.Transformer
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.schema.{ InputObjectType, ObjectType }

import scala.util.chaining._
import utils.graphql.SangriaUtil.instances._

sealed abstract case class Natural(
    nonNegative: BigInt
)

object Natural {

  private def apply(nonNegative: BigInt): Natural = new Natural(nonNegative) {}

  implicit val naturalCodec: Codec[Natural] =
    deriveCodec[Natural].iemap(
      Right(_).filterOrElse(_.nonNegative >= 0, "Non a non-negative number")
    )(identity)

  implicit val fromInternal: Transformer[spire.math.Natural, Natural] =
    _.toBigInt.pipe(Natural.apply)

  implicit val toInternal: Transformer[Natural, spire.math.Natural] =
    _.nonNegative.pipe(spire.math.Natural.apply)

  implicit val objectType: ObjectType[Unit, Natural] = deriveObjectType[Unit, Natural]()

  implicit val inputObjectType: InputObjectType[Natural] = deriveInputObjectType[Natural](
    InputObjectTypeName("NaturalInput")
  )

}
