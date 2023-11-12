package graphql.queries.statistics

import io.scalaland.chimney.Transformer
import math.Positive
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType
import utils.graphql.SangriaUtil.instances._
import utils.math.MathUtil

case class After(
    one: BigDecimal,
    completion: BigDecimal,
    simulation: Option[BigDecimal]
)

object After {

  implicit val fromInternal: Transformer[(processing.statistics.task.After, Positive), After] = {
    case (after, numberOfDecimalPlaces) =>
      val toBigDecimal = MathUtil.rationalToBigDecimal(numberOfDecimalPlaces)
      After(
        one = toBigDecimal(after.one),
        completion = toBigDecimal(after.completion),
        simulation = after.simulation.map(toBigDecimal)
      )

  }

  implicit val objectType: ObjectType[Unit, After] = deriveObjectType[Unit, After]()

}
